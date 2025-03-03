package com.example.MoimMoim.service;

import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.enums.EnumUtils;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import com.example.MoimMoim.exception.member.DuplicateNicknameException;
import com.example.MoimMoim.exception.member.EmailAlreadyExistsException;
import com.example.MoimMoim.exception.member.RoleNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberSignupServiceImpl implements MemberSignupService {

    private static final String EMAIL_ALREADY_EXISTS = "이메일이 이미 존재합니다.";
    private static final String NICKNAME_ALREADY_EXISTS = "이미 사용 중인 닉네임입니다.";
    private static final String ROLE_NOT_FOUND = "해당 권한은 존재하지 않습니다.";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    /**
     * 회원가입
     */
    @Override
    public void signup(MemberSignUpRequestDTO memberSignUpRequestDTO) {
        validateDuplicateMember(memberSignUpRequestDTO);

        Member member = createMember(memberSignUpRequestDTO);
        memberRepository.save(member);
    }

    /**
     * 이메일과 닉네임 중복 검증
     */
    private void validateDuplicateMember(MemberSignUpRequestDTO memberSignUpRequestDTO) {
        if (memberRepository.existsByEmail(memberSignUpRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS);
        }

        if (memberRepository.existsByNickname(memberSignUpRequestDTO.getNickname())) {
            throw new DuplicateNicknameException(NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     * 멤버 객체 생성 및 역할 할당
     */
    private Member createMember(MemberSignUpRequestDTO dto) {
        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));

        return Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // 비밀번호 암호화
                .phone(dto.getPhone())
                .name(dto.getName())
                .gender(EnumUtils.fromLabel(Gender.class, dto.getGender()))
                .nickname(dto.getNickname())
                .birthday(LocalDate.parse(dto.getBirthday()))
                .signupDate(LocalDateTime.now())
                .role(role)
                .build();

    }
}
