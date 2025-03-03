package com.example.MoimMoim.repository;

import com.example.MoimMoim.config.QuerydslConfig;
import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import com.example.MoimMoim.exception.post.CategoryNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QuerydslConfig.class)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Role role;
    private Post post;
    private Post post1, post2, post3;

    @BeforeEach
    void setup() {
        role = new Role(1L, RoleName.ROLE_USER);

        member = Member.builder()
                .email("email@example.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("John Doe")
                .gender(Gender.MALE)
                .nickname("johnny")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(role)
                .signupDate(LocalDateTime.now())
                .build();

        post = Post.builder()
                .member(member)
                .title("게시글 제목")
                .content("게시글 내용")
                .category(Category.ART)
                .createAt(LocalDateTime.now())
                .updateAt(null)
                .comments(new ArrayList<>())
                .viewCount(0L)
                .build();
        memberRepository.save(member);

        // 게시글들 초기화
        post1 = new Post();
        post1.setMember(member);
        post1.setTitle("Spring Boot Introduction");
        post1.setContent("Learn the basics of Spring Boot.");
        post1.setCategory(Category.TECHNOLOGY);
        post1.setCreateAt(LocalDateTime.now());

        post2 = new Post();
        post2.setMember(member);
        post2.setTitle("Spring Security Guide");
        post2.setContent("Secure your Spring application.");
        post2.setCategory(Category.TECHNOLOGY);
        post2.setCreateAt(LocalDateTime.now());

        post3 = new Post();
        post3.setMember(member);
        post3.setTitle("Cooking with Spring");
        post3.setContent("Spring recipes for beginners.");
        post3.setCategory(Category.STUDY);
        post3.setCreateAt(LocalDateTime.now());

        // 게시글을 데이터베이스에 저장
        postRepository.saveAll(List.of(post1, post2, post3));

    }


    @Test
    @DisplayName("게시글 저장 및 조회")
    void postSaveAndById() {

        // when
        Post savedPost = postRepository.save(post);
        Post findPost = postRepository.findById(post.getPostId())
                            .orElseThrow(() -> new PostNotFoundException("찾기 실패"));

        // then
        assertThat(findPost).isNotNull(); // null 여부 확인
        assertThat(findPost.getPostId()).isEqualTo(savedPost.getPostId()); // postId가 동일 여부
        assertThat(findPost.getMember().getMemberId()).isEqualTo(savedPost.getMember().getMemberId()); // 작성자 아이디 동일 여부
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePost() {
        // given
        Post savedPost = postRepository.save(post);

        // when
        postRepository.delete(savedPost);
        Optional<Post> deletedPost = postRepository.findById(savedPost.getPostId());

        // then
        assertThat(deletedPost).isEmpty(); // 삭제된 게시글이 존재하지 않아야 함
    }


    @Test
    @DisplayName("Post 아이디와 Member로 조회")
    void findByPostIdAndMember() {
        // given
        Post savedPost = postRepository.save(post);

        // when
        Optional<Post> byPostIdAndMember = postRepository.findByPostIdAndMember(savedPost.getPostId(), member);

        // then
        assertThat(byPostIdAndMember).isNotEmpty();

    }

//    @Test
//    @DisplayName("카테고리, 키워드 리스트 조회")
//    void findPostsByCategoryAndKeyword() {
//        List<PostSummaryResponseDTO> SizeTECH = postRepository.findPostsByCategoryAndKeyword("TECHNOLOGY",
//                null,
//                "title",
//                "date",
//                PageRequest.of(1 - 1, 40));
//
//        assertThat(SizeTECH).hasSize(2);  // 'Spring Boot Introduction'과 'Spring Security Guide' 두 개의 게시글이 반환됨
//    }
//
//    @Test
//    @DisplayName("카테고리와 키워드로 게시글 조회")
//    void findPostsByCategoryAndKeywordWithKeyword() {
//        List<PostSummaryResponseDTO> filteredPosts = postRepository.findPostsByCategoryAndKeyword(
//                "TECHNOLOGY",
//                "Spring",
//                "content",
//                "date",
//                PageRequest.of(1 - 1, 40)
//        );
//
//        assertThat(filteredPosts).hasSize(2);  // 'Spring Boot Introduction'과 'Spring Security Guide'가 필터링되어 반환됨
//    }
//
//    @Test
//    @DisplayName("카테고리로 게시글 조회")
//    void findPostsByCategoryOnly() {
//        List<PostSummaryResponseDTO> posts = postRepository.findPostsByCategoryAndKeyword(
//                "TECHNOLOGY",
//                null,
//                null,
//                null,
//                PageRequest.of(1-1, 40)
//        );
//
//        assertThat(posts).hasSize(2);  // 'Spring Boot Introduction'과 'Spring Security Guide'만 반환됨
//    }
//
//    @Test
//    @DisplayName("키워드로 게시글 조회")
//    void findPostsByKeywordOnly() {
//        List<PostSummaryResponseDTO> filteredPosts = postRepository.findPostsByCategoryAndKeyword(
//                null,
//                "Spring",
//                "content",
//                "date",
//                PageRequest.of(1-1, 40)
//        );
//
//        assertThat(filteredPosts).hasSize(3);  // 모든 게시글에 'Spring' 키워드가 포함되어 있음
//    }
//
//    @Test
//    @DisplayName("카테고리와 키워드로 필터링된 게시글이 없을 경우")
//    void findPostsByCategoryAndKeywordNoMatch() {
//
//
//        assertThatThrownBy(() ->
//                 postRepository.findPostsByCategoryAndKeyword(
//                        "LIFESTYLE",  // 존재하지 않는 카테고리, 카테고리는 사용자가 조정할 수 없음, exception
//                        "Java",  // 해당 키워드가 게시글에 없음
//                        "title+content",
//                        null,
//                        PageRequest.of(1-1, 40)
//                )
//                ).isInstanceOf(CategoryNotFoundException.class)
//                .hasMessage("카테고리 정보가 없습니다.");
//
//
//    }
//
//    @Test
//    @DisplayName("페이징 처리 테스트")
//    void findPostsWithPagination() {
//        List<PostSummaryResponseDTO> page1 = postRepository.findPostsByCategoryAndKeyword(
//                "TECHNOLOGY",
//                null,
//                null,
//                null,
//                PageRequest.of(1-1, 2)  // 페이지 크기 2
//        );
//
//        assertThat(page1).hasSize(2);  // 첫 번째 페이지는 2개의 게시글만 반환
//
//        List<PostSummaryResponseDTO> page2 = postRepository.findPostsByCategoryAndKeyword(
//                "TECHNOLOGY",
//                null,
//                null,
//                null,
//                PageRequest.of(2-1, 2)  // 두 번째 페이지
//        );
//
//        assertThat(page2).hasSize(0);  // 두 번째 페이지에는 더 이상 게시글이 없음
//    }


}
