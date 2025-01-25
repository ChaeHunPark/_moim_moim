package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.dto.moim.MoimCommentRequestDTO;

public interface MoimCommentService {
    // 댓글 작성
    void createComment(MoimCommentRequestDTO request);

    // 댓글 수정
    void updateComment(MoimCommentRequestDTO request, Long commentId);

    // 댓글 삭제
    void deleteComment(MoimCommentRequestDTO request, Long commentId);
}
