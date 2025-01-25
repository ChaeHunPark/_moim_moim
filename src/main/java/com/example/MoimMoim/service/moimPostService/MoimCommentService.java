package com.example.MoimMoim.service.moimPostService;

import com.example.MoimMoim.dto.moimPost.MoimCommentRequestDTO;
import com.example.MoimMoim.dto.post.CommentRequestDTO;

public interface MoimCommentService {
    // 댓글 작성
    void createComment(MoimCommentRequestDTO request);

    // 댓글 수정
    void updateComment(MoimCommentRequestDTO request, Long commentId);

    // 댓글 삭제
    void deleteComment(MoimCommentRequestDTO request, Long commentId);
}
