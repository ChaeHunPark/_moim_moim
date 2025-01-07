package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.post.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PostExceptionHandler {

    //게시글 조회 예외처리
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글이 존재하지 않습니다.");
    }
}
