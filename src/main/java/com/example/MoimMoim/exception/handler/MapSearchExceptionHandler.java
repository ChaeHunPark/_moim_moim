package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.mapSearch.ClientErrorException;
import com.example.MoimMoim.exception.mapSearch.ServerErrorException;
import com.example.MoimMoim.exception.member.EmailAlreadyExistsException;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class MapSearchExceptionHandler {
    // 이메일 중복 예외처리
    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<?> handleClientErrorException(ClientErrorException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "클라이언트 오류가 발생하였습니다."));
    }

    //멤버 찾기 예외처리
    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<?> handleServerErrorException(ClientErrorException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "서버 오류가 발생하였습니다."));
    }
}
