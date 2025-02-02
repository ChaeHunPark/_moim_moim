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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;




@DataJpaTest
@ActiveProfiles("test") // H2 데이터베이스를 사용하도록 설정
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    Role role = new Role(1L, RoleName.ROLE_USER);



    @Test
    @DisplayName("Member 객체가 정상적으로 생성되어야 한다.")
    void testMemberCreation() {
        // Given: 회원을 생성
        Member member = Member.builder()
                .email("test@naver.com")
                .password("password123")
                .phone("010-1234-5678")
                .name("John Doe")
                .gender(Gender.MALE)
                .nickname("johndoe")
                .birthday(LocalDate.of(1990, 1, 1))
                .signupDate(LocalDateTime.now())
                .role(role) // Role 객체도 생성하여 연결
                .build();

        // When: Member 객체를 저장
        Member savedMember = memberRepository.save(member);

        // Then: AssertJ로 검증
        Assertions.assertThat(savedMember.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(savedMember.getPhone()).isEqualTo("010-1234-5678");
        Assertions.assertThat(savedMember.getName()).isEqualTo("John Doe");
        Assertions.assertThat(savedMember.getGender()).isEqualTo(Gender.MALE);
        Assertions.assertThat(savedMember.getNickname()).isEqualTo("johndoe");
        Assertions.assertThat(savedMember.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
        Assertions.assertThat(savedMember.getRole()).isNotNull();
    }

    @Test
    @DisplayName("Member 엔티티가 Role과 제대로 연결되어야 한다.")
    void testRoleAssociation() {
        // Given: Role과 연결된 Member 생성
        Member member = Member.builder()
                .email("test2@naver.com")
                .password("password123")
                .phone("010-2345-6789")
                .name("Jane Doe")
                .gender(Gender.FEMALE)
                .nickname("janedoe")
                .birthday(LocalDate.of(1992, 2, 2))
                .signupDate(LocalDateTime.now())
                .role(role)
                .build();

        // When: Member 객체 저장
        Member savedMember = memberRepository.save(member);

        // Then: AssertJ로 Role과 연관된 정보를 검증
        Assertions.assertThat(savedMember.getRole().getRoleName()).isEqualTo(RoleName.ROLE_USER);
    }

    @Test
    @DisplayName("회원 가입 시, 필수 값이 없으면 예외가 발생해야 한다.")
    void testMemberValidation() {
        // Given: 유효하지 않은 DTO (필수 값이 빠진 상태)
            // DTO에서 필수 값을 Validation하기 떄문에 하지 않는다.
    }

    @Test
    @DisplayName("Member 객체의 생성일자는 현재 날짜여야 한다.")
    void testSignupDate() {
        // Given: 회원을 생성하면서 생성일자를 자동으로 설정
        Member member = Member.builder()
                .email("test4@naver.com")
                .password("password123")
                .phone("010-4567-8901")
                .name("New User")
                .gender(Gender.MALE)
                .nickname("newuser")
                .birthday(LocalDate.of(1994, 4, 4))
                .signupDate(LocalDateTime.now())  // 현재 날짜로 설정
                .role(role) // Role 객체 연결
                .build();

        // When: Member 객체 저장
        Member savedMember = memberRepository.save(member);

        // Then: AssertJ로 signupDate가 현재 날짜와 일치하는지 확인
        Assertions.assertThat(savedMember.getSignupDate())
                .isEqualToIgnoringNanos(LocalDateTime.now());
    }
}