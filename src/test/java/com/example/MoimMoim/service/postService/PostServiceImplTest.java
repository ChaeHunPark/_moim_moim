package com.example.MoimMoim.service.postService;

import com.example.MoimMoim.domain.*;
import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.exception.post.CategoryNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;
import com.example.MoimMoim.service.utilService.DateTimeUtilService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.beans.Expression;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private DateTimeUtilService dateTimeUtilService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;


    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private Pageable pageable;


    private Member mockMember;
    private Post mockPost;
    private Comment mockComment;
    private PostRequestDTO postRequestDTO;


    @BeforeEach
    void setup() {
        mockMember = Member.builder()
                .memberId(1L)
                .build();

        postRequestDTO = PostRequestDTO.builder()
                .title("제목")
                .category(Category.ART)
                .content("내용")
                .memberId(1L)
                .build();
        ArrayList<Comment> comments = new ArrayList<>();

        mockPost = Post.builder()
                .postId(1L)
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .category(postRequestDTO.getCategory())
                .member(mockMember)
                .createAt(LocalDateTime.now())
                .updateAt(null)
                .comments(comments)
                .viewCount(0L)
                .build();


        mockComment = Comment.builder()
                .member(mockMember)
                .content("댓글")
                .post(mockPost)
                .createAt(LocalDateTime.now())
                .build();

        comments.add(mockComment);

    }





    @Test
    @DisplayName("게시글 저장 서비스 테스트")
    void createPost() {

        //given
        when(memberRepository.findById(mockMember.getMemberId())).thenReturn(Optional.of(mockMember));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        //when
        postService.createPost(postRequestDTO);

        //then, 메서드 호출 검증
        verify(postRepository, times(1)).save(any(Post.class));
        verify(memberRepository,times(1)).findById(mockMember.getMemberId());
    }

    @Test
    @DisplayName("게시글 단건 조회 서비스 테스트")
    void viewPost() {

        //given when
        when(postRepository.findById(mockPost.getPostId())).thenReturn(Optional.of(mockPost));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(dateTimeUtilService.formatForClient(any())).thenReturn("2025.02.09. 11:23");

        PostResponseDTO postResponseDTO = postService.viewPost(mockPost.getPostId());

        // then
        verify(postRepository,times(1)).findById(any(Long.class));
        verify(postRepository,times(1)).save(any(Post.class));
        // 댓글이 1개 이므로 댓글에 포맷팅 1번, 게시글에 포맷팅 1번 총 2번이 일어난다.
        verify(dateTimeUtilService, times(2)).formatForClient(any());
        assertThat(postResponseDTO.getPostId()).isEqualTo(mockPost.getPostId());

    }


    @Test
    @DisplayName("유효한 카테고리로 게시물을 조회한다.")
    public void testFindPostsByValidCategory() {
        String validCategory = "ART"; // 유효한 카테고리 값
        String keyword = null; // 검색 키워드는 null
        Pageable pageable = PageRequest.of(1-1, 10);

        // Mocking: 실제 데이터가 아닌 가짜 데이터를 리턴하도록 설정
        Post post1 = Post.builder()
                .postId(1L)
                .title(postRequestDTO.getTitle())
                .content(postRequestDTO.getContent())
                .category(postRequestDTO.getCategory())
                .member(mockMember)
                .createAt(LocalDateTime.now())
                .updateAt(null)
                .comments(new ArrayList<Comment>())
                .viewCount(0L)
                .build();


        List<Post> posts = List.of(post1, post1);

        List<PostSummaryResponseDTO> postSummaryResponseDTOs =
                posts.stream()
                                .map(post -> {
                                    PostSummaryResponseDTO dto = new PostSummaryResponseDTO();
                                    dto.setPostId(post.getPostId());
                                    dto.setTitle(post.getTitle());
                                    dto.setCategory(post.getCategory());
                                    dto.setCreateAt(post.getCreateAt().toString());
                                    dto.setNickname(post.getMember().getNickname());
                                    dto.setCommentCount(0L);
                                    dto.setViewCount(post.getViewCount());
                                    return dto;
                                }).collect(Collectors.toList());


        when(postRepository.findPostsByCategoryAndKeyword(eq(validCategory), eq(keyword), eq("title"), eq(keyword), any(Pageable.class)))
                .thenReturn(postSummaryResponseDTOs);


        // 서비스 호출
        List<PostSummaryResponseDTO> result = postService.getPostList(validCategory, keyword, keyword,"title",1,30 );

        // 결과 확인
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategory()).isEqualTo(Category.ART); // 유효한 카테고리가 ART여야 한다
        assertThat(result.get(0).getTitle()).isEqualTo(postRequestDTO.getTitle());
        assertThat(result.get(0).getNickname()).isEqualTo(mockMember.getNickname());
    }

    @Test
    @DisplayName("유효하지 않은 카테고리로 게시물을 조회한다.")
    public void testFindPostsByInvalidCategory() {
        String invalidCategory = "INVALID"; // 존재하지 않는 카테고리
        String keyword = null;
        Pageable pageable = PageRequest.of(0, 10);

        // Mock: 유효하지 않은 카테고리로 조회 시 빈 리스트 반환
        when(postRepository.findPostsByCategoryAndKeyword(eq(invalidCategory), eq(keyword), eq("title"), eq(keyword), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        // 서비스 호출
        List<PostSummaryResponseDTO> result = postService.getPostList(invalidCategory, keyword, keyword, "title", 1, 30);

        // 검증: 결과가 비어 있어야 한다.
        assertThat(result).isEmpty();

    }

    @Test
    @DisplayName("잘못된 검색 키워드로 게시물을 조회한다.")
    public void testFindPostsByInvalidKeyword() {
        String validCategory = "ART"; // 유효한 카테고리
        String invalidKeyword = "NonExistentKeyword"; // 존재하지 않는 검색 키워드
        Pageable pageable = PageRequest.of(0, 10);

        // Mock: 존재하지 않는 키워드로 조회 시 빈 리스트 반환
        when(postRepository.findPostsByCategoryAndKeyword(eq(validCategory), eq(invalidKeyword), eq("title"), eq(invalidKeyword), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        // 서비스 호출
        List<PostSummaryResponseDTO> result = postService.getPostList(validCategory, invalidKeyword, invalidKeyword, "title", 1, 30);

        // 검증: 결과가 비어 있어야 한다.
        assertThat(result).isEmpty();
    }


    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }
}