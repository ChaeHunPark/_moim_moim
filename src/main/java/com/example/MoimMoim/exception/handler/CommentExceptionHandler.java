package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.auth.InvalidAuthenticationMethodException;
import com.example.MoimMoim.exception.comment.CommentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommentExceptionHandler {
    // 인증 방식 선택 예외
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글 정보를 찾을 수 없습니다.");
    }
}
