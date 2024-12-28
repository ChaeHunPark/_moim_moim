package com.example.MoimMoim.exception.handler;


import org.springframework.web.bind.annotation.ControllerAdvice;

/*
 * 비즈니스 로직을 실행 중에 커스텀 예외(custom exception)가 발생하면,
 * 해당 예외를 @ExceptionHandler나 **글로벌 예외 처리기 (@ControllerAdvice)**에서 처리하고,
 * 정의한 오류 코드와 메시지를 클라이언트에게 반환함
 *
 * 비지니스 로직의 예외처리답당은 -> 커스텀으로 만든 예외처리
 * 클라이언트가 실행 도중 비지니스 로직의 예외가 일어나면 -> @ExceptionHandler에서 정한 예외를 던져준다
 *
 * */

@ControllerAdvice
public class GlobalExceptionHandler {

}
