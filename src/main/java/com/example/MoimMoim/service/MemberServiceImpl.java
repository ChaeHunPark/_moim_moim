package com.example.MoimMoim.service;

import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.MemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

// 회원가입 서비스 실제 로직 구현 클래스

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public void signup(MemberDTO memberDTO) {
        Member member = Member.builder()
                .email(memberDTO.getEmail())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .phone(memberDTO.getPhone())
                .name(memberDTO.getName())
                .gender(memberDTO.getGender())
                .nickname(memberDTO.getNickname())
                .birthday(memberDTO.getBirthday())
                .signupDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }
}
