package com.example.MoimMoim.exception.auth;

public class InvalidAuthenticationMethodException extends RuntimeException {
    public InvalidAuthenticationMethodException(String message) {
        super(message);
    }
}
