package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Repository 계층 테스트에 최적화된 어노테이션

/*
* Spring Boot가 기본으로 제공하는 내장형 데이터베이스는 H2, HSQL, Derby 이기때문에
* 내장 데이터베이스 자동 구성을 비활성화
* */

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) -> 기본 데이터 베이스를 사용하지 안흔다는 선언.


@DataJpaTest
@ActiveProfiles("test") // H2 데이터베이스를 사용하도록 설정
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    Role role = new Role(1L, RoleName.ROLE_USER);
    Member member;

    @BeforeEach
    void setup() {
        // given
        member = Member.builder()
                .email("email@example.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("John Doe")
                .gender(Gender.MALE)
                .nickname("johnny")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(role)
                .signupDate(LocalDateTime.now())
                .build();
    }


    @Test
    void saveMember() {

        // when
        memberRepository.save(member); // 저장
        Optional<Member> findMember = memberRepository.findById(1L);

        // then
        assertThat(findMember).isNotNull();
        assertThat(findMember.get().getEmail()).isEqualTo("email@example.com");
        assertThat(findMember.get().getName()).isEqualTo("John Doe");
        assertThat(findMember.get().getPassword()).isEqualTo("password123");
    }
}