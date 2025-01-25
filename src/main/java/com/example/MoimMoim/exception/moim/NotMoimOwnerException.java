package com.example.MoimMoim.exception.moim;

public class NotMoimOwnerException extends RuntimeException {
    public NotMoimOwnerException(String message) {
        super(message);
    }
}