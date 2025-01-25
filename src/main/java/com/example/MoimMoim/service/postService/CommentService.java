package com.example.MoimMoim.service.postService;

import com.example.MoimMoim.dto.post.CommentRequestDTO;

public interface CommentService {
    // 댓글 작성
    void createComment(CommentRequestDTO request);

    // 댓글 수정
    void updateComment(CommentRequestDTO request, Long commentId);

    // 댓글 삭제
    void deleteComment(CommentRequestDTO request, Long commentId);
}
