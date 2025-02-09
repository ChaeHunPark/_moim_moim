package com.example.MoimMoim.service.postService;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.domain.QComment;
import com.example.MoimMoim.domain.QPost;
import com.example.MoimMoim.dto.post.CommentResponseDTO;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;
import com.example.MoimMoim.service.utilService.DateTimeUtilService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService{

    private final DateTimeUtilService dateTimeUtilService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PostServiceImpl(DateTimeUtilService dateTimeUtilService, PostRepository postRepository, MemberRepository memberRepository) {
        this.dateTimeUtilService = dateTimeUtilService;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));
    }


    public Post convertPost(PostRequestDTO postRequestDTO){
        return Post.builder()
                    .title(postRequestDTO.getTitle())
                    .category(postRequestDTO.getCategory())
                    .content(postRequestDTO.getContent())
                    .createAt(LocalDateTime.now())
                    .updateAt(null)
                    .viewCount(0L)
                    .member(findMember(postRequestDTO.getMemberId()))
                    .build();
    }




    @Transactional
    @Override
    public void createPost(PostRequestDTO postRequestDTO) {
        Post post = convertPost(postRequestDTO);
        postRepository.save(post);
    }

    // 게시글 단건 조회
    @Override
    public PostResponseDTO viewPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // 게시글 조회 후 카운트 증가 및 데이터베이스에 저장
        post.incrementViewCount();
        postRepository.save(post);

        // 댓글리스트 조회
        List<CommentResponseDTO> comments = post.getComments().stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getCommentId(),
                        comment.getMember().getMemberId(),
                        comment.getContent(),
                        comment.getMember().getNickname(),
                        dateTimeUtilService.formatForClient(comment.getCreateAt())))
                .collect(Collectors.toList());


        PostResponseDTO postResponseDTO = PostResponseDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent())
                .nickname(post.getMember().getNickname())
                .createAt(dateTimeUtilService.formatForClient(post.getCreateAt()))
                .commentList(comments)
                .viewCount(post.getViewCount())
                .build();

        if(post.getUpdateAt() != null) {
            postResponseDTO.setUpdateAt(dateTimeUtilService.formatForClient(post.getUpdateAt()));
        }

        return postResponseDTO;

    }

    // 전체 게시글 조회
    @Override
    public List<PostSummaryResponseDTO> getPostList(String category,
                                                    String sortBy,
                                                    String keyword,
                                                    String searchBy,
                                                    int page,
                                                    int size) {
        Pageable pageable = createPageable(page - 1, size); // 클라이언트는 1페이지 부터 시작이지만 offset은 0부터이기 떄문에 1페이지는 0이다.


        return postRepository.findPostsByCategoryAndKeyword(category, keyword, searchBy, sortBy, pageable);
    }


    @Transactional
    @Override
    public void updatePost(Long postId, PostRequestDTO postRequestDTO) {
        // 1. member 찾기
        Member member = memberRepository.findById(postRequestDTO.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));
        // 2. postId와 member 기준으로 찾기
        Post post = postRepository.findByPostIdAndMember(postId, member)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        // 2. 게시글 수정
        post.setTitle(postRequestDTO.getTitle());
        post.setCategory(postRequestDTO.getCategory());
        post.setContent(post.getContent());
        post.setUpdateAt(LocalDateTime.now());

    }

    @Transactional
    @Override
    public void deletePost(Long postId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findByPostIdAndMember(postId, member)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        postRepository.delete(post);
    }

    // Pageable 유효성 검사 메서드
    public Pageable createPageable(int page, int size) {
        // offset이 -1인 경우, 0으로 변경
        int correctedPage = (page < 0) ? 0 : page;

        // size가 30이나 60이 아닌 경우, 30으로 설정
        int correctedSize = (size == 30 || size == 60) ? size : 30;

        return PageRequest.of(correctedPage, correctedSize);
    }

}
