package com.example.MoimMoim.repository;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Role;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.enums.RoleName;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Repository 계층 테스트에 최적화된 어노테이션

/*
* Spring Boot가 기본으로 제공하는 내장형 데이터베이스는 H2, HSQL, Derby 이기때문에
* 내장 데이터베이스 자동 구성을 비활성화
* */

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) -> 기본 데이터 베이스를 사용하지 안흔다는 선언.


@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    Role role = new Role(1L, RoleName.ROLE_USER);

    Member member;
    Member savedMember;

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
        savedMember = memberRepository.save(member); // 저장
    }


    @Test
    @DisplayName("회원 저장 및 Id로 조회")
    void saveAndFindById() {

        // when
        Member savedMember = memberRepository.save(member);// 저장
        Member findMember = memberRepository.findById(savedMember.getMemberId())
                .orElseThrow(() -> new NoSuchElementException("찾기 실패"));

        // then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(findMember.getName()).isEqualTo(savedMember.getName());
        assertThat(findMember.getPassword()).isEqualTo(savedMember.getPassword());
    }

    @Test
    @DisplayName("회원 저장 및 email로 조회")
    void saveAndFindByEmail() {

        // when
        Member findMember = memberRepository.findByEmail(savedMember.getEmail())
                .orElseThrow(() -> new NoSuchElementException("찾기 실패"));

        // then
        assertThat(findMember).isNotNull();
        assertThat(findMember.getEmail()).isEqualTo(savedMember.getEmail());
        assertThat(findMember.getName()).isEqualTo(savedMember.getName());
        assertThat(findMember.getPassword()).isEqualTo(savedMember.getPassword());
    }

    @Test
    @DisplayName("회원 저장 실패 - Role이 빈 객체")
    void shouldFailWhenRoleIsEmpty() {
        Role emptyRole = new Role();

        Member emptyRoleMember = Member.builder()
                .email("email@example.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("John Doe")
                .gender(Gender.MALE)
                .nickname("johnny")
                .birthday(LocalDate.of(1990, 1, 1))
                .role(emptyRole)
                .signupDate(LocalDateTime.now())
                .build();

        assertThatThrownBy(() ->
                memberRepository.save(emptyRoleMember)
                ).isInstanceOf(InvalidDataAccessApiUsageException.class);

    }

    @Test
    @DisplayName("회원 저장 실패 - 중복 저장")
    void shouldFailWhenEmailIsDuplicate() {
        // given
        Member duplicateMember = Member.builder()
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

        assertThatThrownBy(() ->
                memberRepository.save(duplicateMember)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }



}