package com.example.MoimMoim.exception.auth;

// 인증번호가 없을 경우 예외
public class VerificationCodeNotFoundException extends RuntimeException {
    public VerificationCodeNotFoundException(String message) {
        super(message);
    }
}
