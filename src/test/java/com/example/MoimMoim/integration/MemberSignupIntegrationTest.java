package com.example.MoimMoim.integration;


import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberSignupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 요청 성공 테스트")
    void userSignupTest() throws Exception {

        //given
        String email = "test@example.com";
        String password = "Aa!12345";

        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "phone": "010-1234-5678",
                    "name": "홍길동",
                    "gender": "MALE",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(email, password);


        //when
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("회원가입이 완료되었습니다."));


        // Then (DB에서 회원 정보 확인)
        Member savedUser = memberRepository.findByEmail(email).orElseThrow();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getNickname()).isEqualTo("길동이");
        assertThat(passwordEncoder.matches(password, savedUser.getPassword())).isTrue(); // 비밀번호 암호화 테스트

    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패")
    void userSignupValidationFailTest() throws Exception {
        // Given (잘못된 입력값)
        String requestBody = """
            {
                "email": "testexample.com",
                "password": "password",
                "phone": "01012345678",
                "name": "홍",
                "gender": "MALE",
                "nickname": "길",
                "birthday": "19950815"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())  // 400 Bad Request 기대
                .andExpect(jsonPath("$.email").value("이메일 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 8자 이상이어야 하며, 소문자, 대문자, 특수문자를 포함해야 합니다."))
                .andExpect(jsonPath("$.phone").value("전화번호 형식이 올바르지 않습니다."))
                .andExpect(jsonPath("$.birthday").value("생년월일은 YYYY-MM-DD 형식이어야 합니다."));
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 실패 테스트")
    void emailDuplicateTest() throws Exception {
        // 먼저 정상적으로 회원가입을 수행
        String requestBody = """
            {
                "email": "test@example.com",
                "password": "Aa!12345",
                "phone": "010-1234-5678",
                "name": "홍길동",
                "gender": "MALE",
                "nickname": "길동이",
                "birthday": "1995-08-15"
            }
            """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // 같은 이메일로 다시 회원가입 시도
        String duplicateEmailRequestBody = """
            {
                "email": "test@example.com",
                "password": "Bb!12345",
                "phone": "010-1111-2222",
                "name": "김철수",
                "gender": "MALE",
                "nickname": "철수",
                "birthday": "1992-05-10"
            }
            """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmailRequestBody))
                .andExpect(status().isBadRequest()) // 409 Conflict 응답 기대
                .andExpect(content().string("이메일이 이미 존재합니다."));
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입 실패 테스트")
    void nicknameDuplicateTest() throws Exception {
        // 먼저 정상적으로 회원가입을 수행
        String requestBody = """
            {
                "email": "user1@example.com",
                "password": "Aa!12345",
                "phone": "010-1234-5678",
                "name": "홍길동",
                "gender": "MALE",
                "nickname": "길동이",
                "birthday": "1995-08-15"
            }
            """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // 같은 닉네임으로 다시 회원가입 시도
        String duplicateNicknameRequestBody = """
            {
                "email": "user2@example.com",
                "password": "Bb!12345",
                "phone": "010-9876-5432",
                "name": "김철수",
                "gender": "MALE",
                "nickname": "길동이",
                "birthday": "1992-05-10"
            }
            """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateNicknameRequestBody))
                .andExpect(status().isBadRequest()) // 409 Conflict 응답 기대
                .andExpect(content().string("이미 사용 중인 닉네임 입니다."));
    }


}
