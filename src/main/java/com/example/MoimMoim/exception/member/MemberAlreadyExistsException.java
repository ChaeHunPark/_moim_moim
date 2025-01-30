package com.example.MoimMoim.exception.member;

public class MemberAlreadyExistsException extends RuntimeException{
    public MemberAlreadyExistsException(String message) {
        super(message);
    }
}
