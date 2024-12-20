package com.example.MoimMoim.service;

import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.MemberRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

// 멤버 서비스 실제 로직 구현 클래스

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public void signup(MemberRequestDTO memberRequestDTO) {
        Member member = Member.builder()
                .email(memberRequestDTO.getEmail())
                .password(passwordEncoder.encode(memberRequestDTO.getPassword()))
                .phone(memberRequestDTO.getPhone())
                .name(memberRequestDTO.getName())
                .gender(memberRequestDTO.getGender())
                .nickname(memberRequestDTO.getNickname())
                .birthday(memberRequestDTO.getBirthday())
                .signupDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }
}
