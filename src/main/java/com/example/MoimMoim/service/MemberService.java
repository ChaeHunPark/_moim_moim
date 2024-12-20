package com.example.MoimMoim.service;

import com.example.MoimMoim.dto.MemberRequestDTO;


// 회원가입 서비스 인터페이스
public interface MemberService {
    void signup(MemberRequestDTO memberRequestDTO);
}
