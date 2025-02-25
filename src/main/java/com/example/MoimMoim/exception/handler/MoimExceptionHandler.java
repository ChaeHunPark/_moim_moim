package com.example.MoimMoim.exception.handler;

import com.example.MoimMoim.exception.moim.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class MoimExceptionHandler {

    // 이미 신청한 모임 예외처리
    @ExceptionHandler(AlreadyAppliedException.class)
    public ResponseEntity<?> handleAlreadyAppliedException(AlreadyAppliedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "이미 신청한 모임입니다."));
    }

    // 모임 인원마감 예외처리
    @ExceptionHandler(MoimMaxParticipantsReachedException.class)
    public ResponseEntity<?> handleMoimMaxParticipantsReachedException(MoimMaxParticipantsReachedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "인원이 마감되었습니다."));
    }

    // 잘못된 접근
    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<?> handleInvalidAccessException(InvalidAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "잘못된 접근입니다."));
    }

    // 신청 정보 존재하지 않음
    @ExceptionHandler(MoimParticipationNotFoundException.class)
    public ResponseEntity<?> handleMoimParticipationNotFoundException(MoimParticipationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "신청 정보가 존재하지 않습니다."));
    }

    // 이미 수락 또는 거절된 모임
    @ExceptionHandler(ParticipationStatusFinalException.class)
    public ResponseEntity<?> handleParticipationStatusFinalException(ParticipationStatusFinalException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "이미 상태가 변경된 신청입니다. 더 이상 수락 또는 거절할 수 없습니다."));
    }

    // 찾을 수 없는 신청
    @ExceptionHandler(ParticipationNotFoundException.class)
    public ResponseEntity<?> handleParticipationNotFoundException(ParticipationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "참여 신청을 찾을 수 없습니다."));
    }

    // 주최자가 아님
    @ExceptionHandler(MoimOwnerMismatchException.class)
    public ResponseEntity<?> handleMoimOwnerMismatchException(MoimOwnerMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "이 모임의 주최자가 아닙니다."));
    }


}
