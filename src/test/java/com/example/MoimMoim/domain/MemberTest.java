package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class MemberTest {
    @Test
    @DisplayName("Member 도메인 정보 설정 및 확인 테스트")
    void test(){
        // given
        Member member = Member.builder()
                .email("email@example.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("John Doe")
                .gender(Gender.MALE)
                .nickname("johnny")
                .birthday(LocalDate.of(1990, 1, 1))
                .signupDate(LocalDateTime.now())
                .build();

        // when
        String email = member.getEmail();
        String password = member.getPassword();
        String phone = member.getPhone();
        String name = member.getName();
        Gender gender = member.getGender();
        String nickname = member.getNickname();
        LocalDate birthday = member.getBirthday();

        // then
        assertThat(email).isEqualTo("email@example.com");
        assertThat(password).isEqualTo("password123");
        assertThat(phone).isEqualTo("010-1234-5678");
        assertThat(name).isEqualTo("John Doe");
        assertThat(gender).isEqualTo(Gender.MALE);
        assertThat(nickname).isEqualTo("johnny");
        assertThat(birthday).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(member.getSignupDate()).isNotNull(); // 가입일은 현재 시간 기준으로 생성되었는지 확인
    }
}