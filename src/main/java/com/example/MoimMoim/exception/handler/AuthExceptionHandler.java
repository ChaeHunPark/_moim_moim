package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class AuthExceptionHandler {

    //인증 요청을 자주 시도한 것에 대한 예외
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<?> handleTooManyRequestsException(TooManyRequestsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","인증번호를 너무 자주 요청하셨습니다. 잠시 후 다시 시도해주세요."));
    }

    // 인증 방식 선택 예외
    @ExceptionHandler(InvalidAuthenticationMethodException.class)
    public ResponseEntity<?> handleInvalidAuthenticationMethodException(InvalidAuthenticationMethodException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","인증 방식이 잘못되었습니다."));
    }

    // 인증정보가 없을 경우 예외
    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<?> handleVerificationCodeNotFound(VerificationCodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","인증정보가 존재하지 않습니다."));
    }

    // 인증번호가 만료되었을 경우 예외
    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<?> handleVerificationCodeExpired(VerificationCodeExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of("error","인증번호가 만료되었습니다."));
    }

    // 인증번호 불일치 예외
    @ExceptionHandler(VerificationCodeMismatchException.class)
    public ResponseEntity<?> handleVerificationCodeMismatch(VerificationCodeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","인증번호가 일치하지 않습니다."));
    }
}
