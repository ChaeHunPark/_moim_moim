package com.example.MoimMoim.service.postService;

import com.example.MoimMoim.domain.Comment;
import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.dto.post.CommentRequestDTO;
import com.example.MoimMoim.exception.comment.CommentNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.CommentRepository;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final String POST_NOT_FOUND = "게시글이 존재하지 않습니다.";
    private static final String MEMBER_NOT_FOUND = "회원정보가 일치하지 않습니다.";
    private static final String COMMENT_NOT_FOUND = "댓글 정보를 찾을 수 없습니다.";

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 게시글 존재 여부 확인
     */
    private Post validatePostExistence(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));
    }

    /**
     * 사용자 존재 여부 확인
     */
    private Member validateMemberExistence(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND));
    }

    /**
     * 댓글 존재 여부 확인
     */
    private Comment validateCommentExistence(Long commentId, Member member) {
        return commentRepository.findByCommentIdAndMember(commentId, member)
                .orElseThrow(() -> new CommentNotFoundException(COMMENT_NOT_FOUND));
    }

    /**
     * 댓글 작성
     */
    @Override
    public void createComment(CommentRequestDTO commentRequestDTO) {
        Post post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());

        Comment comment = Comment.builder()
                .content(commentRequestDTO.getContent())
                .member(member)
                .post(post)
                .createAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }

    /**
     * 댓글 수정
     */
    @Override
    public void updateComment(CommentRequestDTO commentRequestDTO, Long commentId) {
        Post post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());
        Comment comment = validateCommentExistence(commentId, member);

        comment.setContent(commentRequestDTO.getContent());
    }

    /**
     * 댓글 삭제
     */
    @Override
    public void deleteComment(CommentRequestDTO commentRequestDTO, Long commentId) {
        Post post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());
        Comment comment = validateCommentExistence(commentId, member);

        commentRepository.delete(comment);
    }
}

