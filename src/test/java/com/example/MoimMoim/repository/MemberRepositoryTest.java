package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// Repository 계층 테스트에 최적화된 어노테이션
@DataJpaTest
/*
* Spring Boot가 기본으로 제공하는 내장형 데이터베이스는 H2, HSQL, Derby 이기때문에
* 내장 데이터베이스 자동 구성을 비활성화
* */

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;



    @Test
    void saveMember() {
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
        Member savedMember = memberRepository.save(member); // 저장

        // then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("email@example.com");
        assertThat(savedMember.getName()).isEqualTo("John Doe");
        assertThat(savedMember.getPassword()).isEqualTo("password123");
    }
}