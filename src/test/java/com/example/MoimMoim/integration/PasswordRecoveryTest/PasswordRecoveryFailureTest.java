package com.example.MoimMoim.integration.PasswordRecoveryTest;

import com.example.MoimMoim.service.authService.PasswordRecoveryServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PasswordRecoveryFailureTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean // 실제 객체를 가져오지만, 가상 응답 만들수 있음!
    private PasswordRecoveryServiceImpl passwordRecoveryService;

    @MockitoSpyBean
    private JavaMailSender javaMailSender;

    String invalidEmail = "notexist@naver.com"; // 존재하지 않는 계정
    String validEmail = "cogns8571@naver.com"; // 정상 계정

    String name = "홍길동";

    String invalidCode = "654321"; // 잘못된 인증 코드
    final String CODE = "123456"; // 정상적인 코드

    String weakPassword = "12345"; // 유효하지 않은 비밀번호


    @BeforeEach
    void setup() throws Exception {
        //given
        String requestBody = """
                {
                    "email": "%s",
                    "password": "QWEr1234@",
                    "phone": "010-1234-5678",
                    "name": "%s",
                    "gender": "남자",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(validEmail, name);


        //when
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));
    }

    @Test
    @DisplayName("계정이 존재하지 않을 때 실패")
    void shouldFailWhenAccountDoesNotExist() throws Exception {
        String requestBody = """
                {
                    "name": "%s",
                    "email": "%s"
                }
                """.formatted(name, invalidEmail);

        mockMvc.perform(post("/api/password-recovery/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound()) // 존재하지 않는 계정이라 실패
                .andExpect(jsonPath("$.error").value("회원정보가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("잘못된 인증 방법 선택 시 실패")
    void shouldFailWhenInvalidRecoveryMethodIsChosen() throws Exception {
        String requestBody = """
                {
                    "email": "%s",
                    "method": "sms"
                }
                """.formatted(validEmail); // 현재는 email만 지원, sms는 실패

        mockMvc.perform(post("/api/password-recovery/choose-method")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.method").value("인증 방법이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("잘못된 인증 코드 입력 시 실패")
    void shouldFailWhenWrongVerificationCodeIsUsed() throws Exception {
        String requestBody_chooseMethod = """
                {
                    "email" : "%s",
                    "method" : "email"
                }
                """.formatted(validEmail);

        String requestBody = """
                {
                    "email": "%s",
                    "userInputCode": "%s"
                }
                """.formatted(validEmail, invalidCode); // 잘못된 인증 코드 입력

        // 실제 메일은 전송하지 않도록 한다. (실행 시간이 오래걸린다.)
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // 인증 코드 전송은 123456으로 전송한다.
        when(passwordRecoveryService.generateVerificationCode()).thenReturn(CODE);


        // 1. 인증 코드 전송
        mockMvc.perform(post("/api/password-recovery/choose-method")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody_chooseMethod))
                .andExpect(status().isOk());


        //1.2. 인증 코드 검증 - 인증 코드가 잘못되었을 때
        mockMvc.perform(post("/api/password-recovery/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().is4xxClientError()) // 인증 실패
                .andExpect(jsonPath("$.error").value("인증번호가 일치하지 않습니다."));
    }

    @Test
    @DisplayName("비밀번호 변경 시 유효하지 않은 비밀번호 입력으로 실패")
    void shouldFailWhenWeakPasswordIsProvided() throws Exception {
        String requestBody = """
                {
                    "email": "%s",
                    "newPassword": "%s"
                }
                """.formatted(validEmail, weakPassword); // 비밀번호가 보안 기준에 부합하지 않음

        mockMvc.perform(post("/api/password-recovery/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.newPassword").value("비밀번호는 8자 이상이어야 하며, 소문자, 대문자, 특수문자를 포함해야 합니다."));
    }


}
