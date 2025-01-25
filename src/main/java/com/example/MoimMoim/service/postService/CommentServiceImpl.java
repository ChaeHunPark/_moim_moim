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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService{

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(MemberRepository memberRepository, CommentRepository commentRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    // 게시글 존재 여부 확인 메서드
    private Post validatePostExistence(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    // 사용자 존재 여부 확인 메서드
    private Member validateMemberExistence(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    // 댓글 존재 여부 확인 메서드
    private Comment validateCommentExistence(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));
    }



    // 댓글 작성
    @Transactional
    @Override
    public void createComment(CommentRequestDTO commentRequestDTO) {
        // 게시글과 사용자를 각각 확인
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


    // 댓글 수정
    @Transactional
    @Override
    public void updateComment(CommentRequestDTO commentRequestDTO, Long commentId) {
        // 게시글과 사용자, 댓글 정보 확인
        Post post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());
        Comment comment = commentRepository.findByCommentIdAndMember(commentId, member)
                        .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        comment.setContent(commentRequestDTO.getContent());

    }

    @Transactional
    @Override
    public void deleteComment(CommentRequestDTO commentRequestDTO, Long commentId) {
        Post post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());
        Comment comment = commentRepository.findByCommentIdAndMember(commentId, member)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        commentRepository.delete(comment);

    }

    // 댓글 삭제


}
