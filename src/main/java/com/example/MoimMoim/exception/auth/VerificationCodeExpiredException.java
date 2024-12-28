package com.example.MoimMoim.exception.auth;

// 인증번호가 만료되었을 경우 예외
public class VerificationCodeExpiredException extends RuntimeException {
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}

