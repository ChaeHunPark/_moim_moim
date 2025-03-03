package com.example.MoimMoim.service.postService;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.domain.QComment;
import com.example.MoimMoim.domain.QPost;
import com.example.MoimMoim.dto.post.*;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.EnumUtils;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private static final String MEMBER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String POST_NOT_FOUND = "게시글을 찾을 수 없습니다.";

    private final DateTimeUtilService dateTimeUtilService;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     * 회원 찾기
     */
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));
    }

    /**
     * 게시글 변환
     */
    private Post convertPost(PostRequestDTO dto) {
        return Post.builder()
                .title(dto.getTitle())
                .category(EnumUtils.fromLabel(Category.class,dto.getCategory()))
                .content(dto.getContent())
                .createAt(LocalDateTime.now())
                .viewCount(0L)
                .member(findMember(dto.getMemberId()))
                .build();
    }

    /**
     * 게시글 생성
     */
    @Override
    public void createPost(PostRequestDTO postRequestDTO) {
        postRepository.save(convertPost(postRequestDTO));
    }

    /**
     * 게시글 조회 및 조회수 증가
     */
    @Override
    public PostResponseDTO viewPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

        post.incrementViewCount(); // 조회수 증가
        postRepository.save(post);

        return convertToPostResponseDTO(post);
    }

    /**
     * 게시글 응답 변환
     */
    private PostResponseDTO convertToPostResponseDTO(Post post) {
        List<CommentResponseDTO> comments = post.getComments().stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getCommentId(),
                        comment.getMember().getMemberId(),
                        comment.getContent(),
                        comment.getMember().getNickname(),
                        dateTimeUtilService.formatForClient(comment.getCreateAt())))
                .collect(Collectors.toList());

        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .category(post.getCategory().getLabel())
                .content(post.getContent())
                .nickname(post.getMember().getNickname())
                .createAt(dateTimeUtilService.formatForClient(post.getCreateAt()))
                .updateAt(post.getUpdateAt() != null ? dateTimeUtilService.formatForClient(post.getUpdateAt()) : null)
                .viewCount(post.getViewCount())
                .commentList(comments)
                .build();
    }

    /**
     * 전체 게시글 조회
     */
    @Override
    public PostPageResponseDTO<PostSummaryResponseDTO> getPostList(String category, String sortBy, String keyword, String searchBy, int page, int size) {
        Pageable pageable = createPageable(page, size);
        return postRepository.findPostsByCategoryAndKeyword(category, keyword, searchBy, sortBy, pageable);
    }

    /**
     * 게시글 수정
     */
    @Override
    public void updatePost(Long postId, PostRequestDTO postRequestDTO) {
        Member member = findMember(postRequestDTO.getMemberId());
        Post post = postRepository.findByPostIdAndMember(postId, member)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

        post.update(postRequestDTO.getTitle(),
                EnumUtils.fromLabel(Category.class,postRequestDTO.getCategory()),
                postRequestDTO.getContent());
    }

    /**
     * 게시글 삭제
     */
    @Override
    public void deletePost(Long postId, Long memberId) {
        Member member = findMember(memberId);
        Post post = postRepository.findByPostIdAndMember(postId, member)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

        postRepository.delete(post);
    }

    /**
     * 페이지네이션 유효성 검사 및 생성
     */
    public Pageable createPageable(int page, int size) {
        return PageRequest.of(Math.max(page - 1, 0), (size == 30 || size == 60) ? size : 30);
    }
}

