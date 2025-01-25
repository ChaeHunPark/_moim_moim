package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.member.EmailAlreadyExistsException;
import com.example.MoimMoim.exception.member.MemberAlreadyExistsException;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MemberExceptionHandler {

    // 이메일 중복 예외처리
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일이 이미 존재합니다.");
    }

    //멤버 중복 예외처리
    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<String> handleMemberAlreadyExistsException(MemberAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보가 이미 존재합니다.");
    }

    //멤버 찾기 예외처리
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원정보가 일치하지 않습니다.");
    }
}
