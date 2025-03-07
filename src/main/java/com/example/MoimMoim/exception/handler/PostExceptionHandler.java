package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.post.CategoryNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class PostExceptionHandler {

    //게시글 조회 예외처리
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<?> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "게시글이 존재하지 않습니다."));
    }

    // 카테고리 예외 처리
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleCategoryNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "카테고리 정보가 없습니다."));
    }

}
