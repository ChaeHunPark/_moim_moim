package com.example.MoimMoim.exception.auth;

// 인증번호 불일치 예외
public class VerificationCodeMismatchException extends RuntimeException {
    public VerificationCodeMismatchException(String message) {
        super(message);
    }
}
