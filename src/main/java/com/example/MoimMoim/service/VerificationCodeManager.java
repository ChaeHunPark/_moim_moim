package com.example.MoimMoim.service;

public interface VerificationCodeManager {
        /**
         * 인증번호를 저장
         */
        void saveVerificationCode(String email, String code);

        /**
         * 인증번호의 유효성을 검사
         */
        void validateVerificationCode(String email, String userInputCode);

        /**
         * 새로운 인증번호 요청이 가능한지 확인
         */
        boolean canRequestNewCode(String email);

        /**
         * 인증번호를 삭제 (인증 성공 후 호출).
         */
        void removeVerificationCode(String email);

}
