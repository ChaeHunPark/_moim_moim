package com.example.MoimMoim.service;

import com.example.MoimMoim.exception.member.EmailAlreadyExistsException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;

// 멤버 서비스 실제 로직 구현 클래스

@Transactional // 무결성 보장
@Service
public class MemberSignupServiceImpl implements MemberSignupService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberSignupServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 멤버 객체 생성
    private Member convertToMember(MemberSignUpRequestDTO memberSignUpRequestDTO) {
        return Member.builder()
                .email(memberSignUpRequestDTO.getEmail())
                .password(passwordEncoder.encode(memberSignUpRequestDTO.getPassword())) // 암호화된 비밀번호
                .phone(memberSignUpRequestDTO.getPhone())
                .name(memberSignUpRequestDTO.getName())
                .gender(memberSignUpRequestDTO.getGender())
                .nickname(memberSignUpRequestDTO.getNickname())
                .birthday(LocalDate.parse(memberSignUpRequestDTO.getBirthday()))
                .signupDate(LocalDateTime.now())
                .build();
    }


    /*
    * 회원가입
    * */

    @Override
    public void signup(MemberSignUpRequestDTO memberSignUpRequestDTO) {

        if(memberRepository.existsByEmail(memberSignUpRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("이메일이 이미 존재합니다.");
        }

        Member member = convertToMember(memberSignUpRequestDTO);
        memberRepository.save(member);
    }




}
