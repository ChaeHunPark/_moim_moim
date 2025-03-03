package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.domain.*;
import com.example.MoimMoim.dto.moimPost.MoimCommentRequestDTO;
import com.example.MoimMoim.exception.comment.CommentNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class MoimCommentServiceImpl implements MoimCommentService{
    private final MemberRepository memberRepository;
    private final MoimCommentRepository moimCommentRepository;
    private final MoimPostRepository moimPostRepository;

    @Autowired
    public MoimCommentServiceImpl(MemberRepository memberRepository, MoimCommentRepository moimCommentRepository, MoimPostRepository moimPostRepository) {
        this.memberRepository = memberRepository;
        this.moimCommentRepository = moimCommentRepository;
        this.moimPostRepository = moimPostRepository;
    }

    // 게시글 존재 여부 확인 메서드
    private MoimPost validatePostExistence(Long postId) {
        return moimPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    // 사용자 존재 여부 확인 메서드
    private Member validateMemberExistence(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    // 댓글 존재 여부 확인 메서드
    private MoimPostComment validateCommentExistence(Long commentId){
        return moimCommentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));
    }



    // 댓글 작성
    @Transactional
    @Override
    public void createComment(MoimCommentRequestDTO commentRequestDTO) {
        // 게시글과 사용자를 각각 확인
        MoimPost post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());

        MoimPostComment moimPostComment = MoimPostComment.builder()
                .content(commentRequestDTO.getContent())
                .member(member)
                .moimPost(post)
                .createAt(LocalDateTime.now())
                .build();

        moimCommentRepository.save(moimPostComment);

    }


    // 댓글 수정
    @Transactional
    @Override
    public void updateComment(MoimCommentRequestDTO commentRequestDTO, Long commentId) {
        // 게시글과 사용자, 댓글 정보 확인
        MoimPost post = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());
        MoimPostComment moimPostComment = moimCommentRepository.findByMoimCommentIdAndMember(commentId, member)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        moimPostComment.setContent(commentRequestDTO.getContent());

    }

    @Transactional
    @Override
    public void deleteComment(MoimCommentRequestDTO commentRequestDTO, Long commentId) {
        MoimPost moimPost = validatePostExistence(commentRequestDTO.getPostId());
        Member member = validateMemberExistence(commentRequestDTO.getMemberId());
        MoimPostComment moimPostComment = moimCommentRepository.findByMoimCommentIdAndMember(commentId, member)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        moimCommentRepository.delete(moimPostComment);

    }
}
