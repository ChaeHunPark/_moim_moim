package com.example.MoimMoim.domain;

import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.RoleRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class MemberTest {


    Role role = new Role(1L, RoleName.ROLE_USER);

    @Test
    @DisplayName("회원 엔티티 생성 테스트")
    void createMember() {
        // given
        LocalDate birthday = LocalDate.of(1995, 5, 20);
        LocalDateTime signupDate = LocalDateTime.now();

        Member member = Member.builder()
                .email("test@example.com")
                .password("securePassword123")
                .phone("010-1234-5678")
                .name("홍길동")
                .gender(Gender.MALE)
                .nickname("길동이")
                .birthday(birthday)
                .signupDate(signupDate)
                .role(role)
                .build();// 단순 객체 주입

        // when & then
        assertThat(member.getEmail()).isEqualTo("test@example.com");
        assertThat(member.getPhone()).isEqualTo("010-1234-5678");
        assertThat(member.getNickname()).isEqualTo("길동이");
        assertThat(member.getGender()).isEqualTo(Gender.MALE);
        assertThat(member.getBirthday()).isEqualTo(birthday);
        assertThat(member.getSignupDate()).isEqualTo(signupDate);
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    void updateMemberInfo() {
        // given
        Member member = Member.builder()
                .email("test@example.com")
                .password("securePassword123")
                .phone("010-1234-5678")
                .name("홍길동")
                .gender(Gender.MALE)
                .nickname("길동이")
                .birthday(LocalDate.of(1995, 5, 20))
                .signupDate(LocalDateTime.now())
                .role(role)
                .build();

        // when
        member.setNickname("새로운닉네임");
        member.setPhone("010-9876-5432");

        // then
        assertThat(member.getNickname()).isEqualTo("새로운닉네임");
        assertThat(member.getPhone()).isEqualTo("010-9876-5432");
    }


}