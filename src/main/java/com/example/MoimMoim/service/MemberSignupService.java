package com.example.MoimMoim.service;

import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;


// 회원가입 서비스 인터페이스
public interface MemberSignupService {
    // 회원가입 서비스
    void signup(MemberSignUpRequestDTO memberSignUpRequestDTO);
}
