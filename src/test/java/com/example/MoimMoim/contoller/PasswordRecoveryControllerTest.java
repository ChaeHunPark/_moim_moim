package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.config.TestConfig;
import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.CodeVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.RecoveryMethodRequestDTO;
import com.example.MoimMoim.service.authService.PasswordRecoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Collections;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(TestConfig.class)
@WebMvcTest(PasswordRecoveryController.class) // 컨트롤러만 로드하여 테스트한다.
class PasswordRecoveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PasswordRecoveryService passwordRecoveryService;

    @MockitoBean
    private ValidationService validationService;

    @Test
    @DisplayName("계정 존재 확인 - 성공")
    void checkAccountExistence_ShouldReturnOk() throws Exception {

        //given
        AccountVerificationRequestDTO requestDTO = new AccountVerificationRequestDTO(
                "name","cogns8571@naver.com");


        // validationService가 항상 빈 에러 리스트를 반환하도록 Mock 설정
        given(validationService.validate(any(BindingResult.class)))
                .willReturn(Collections.emptyMap());

        // when then
        mockMvc.perform(post("/api/password-recovery/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(System.out::println)
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("계정 존재 확인 - 실패 (유효하지 않은 요청)")
    void checkAccountExistence_ShouldReturnBadRequest() throws Exception {
        //given
        AccountVerificationRequestDTO requestDTO = new AccountVerificationRequestDTO("","");

        //when
        //validation 실행시 아래의 값을 반환한다고 가정
        when(validationService.validate(any())).thenReturn(Collections.singletonMap("email", "이메일은 필수 입력 항목입니다."));
        doNothing().when(passwordRecoveryService).isAccountExists(any());

        //then
        mockMvc.perform(post("/api/password-recovery/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일은 필수 입력 항목입니다."));
    }

    @Test
    @DisplayName("인증 방법 선택 - 성공")
    void chooseRecoveryMethod_ShouldReturnOk() throws Exception {

        RecoveryMethodRequestDTO requestDTO = new RecoveryMethodRequestDTO("test@example.com", "EMAIL");

        when(validationService.validate(any())).thenReturn(Collections.emptyMap());
        doNothing().when(passwordRecoveryService).selectRecoveryMethodAndSendCode(any());

        mockMvc.perform(post("/api/password-recovery/choose-method")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 전송되었습니다."));
    }

    @Test
    @DisplayName("인증 방법 선택 - 실패 (유효하지 않은 요청)")
    void chooseRecoveryMethod_ShouldReturnBadRequest() throws Exception {
        RecoveryMethodRequestDTO requestDTO = new RecoveryMethodRequestDTO("", "");

        when(validationService.validate(any())).thenReturn(Collections.singletonMap("method", "인증 방법을 선택해야 합니다."));

        mockMvc.perform(post("/api/password-recovery/choose-method")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.method").value("인증 방법을 선택해야 합니다."));
    }

    @Test
    @DisplayName("인증 코드 검증 - 성공")
    void verifyRecoveryCode_ShouldReturnOk() throws Exception {
        CodeVerificationRequestDTO requestDTO = new CodeVerificationRequestDTO("test@example.com", "123456");

        when(validationService.validate(any())).thenReturn(Collections.emptyMap());

        doNothing().when(passwordRecoveryService).verifyCode(any());

        mockMvc.perform(post("/api/password-recovery/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 확인되었습니다. 비밀번호를 재설정해주세요."));
    }

    @Test
    @DisplayName("인증 코드 검증 - 실패 (잘못된 코드)")
    void verifyRecoveryCode_ShouldReturnBadRequest() throws Exception {
        CodeVerificationRequestDTO requestDTO = new CodeVerificationRequestDTO("test@example.com", "");
        when(validationService.validate(any())).thenReturn(Collections.singletonMap("code", "인증 코드를 입력해야 합니다."));

        mockMvc.perform(post("/api/password-recovery/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("인증 코드를 입력해야 합니다."));
    }

    @Test
    @DisplayName("비밀번호 재설정 - 성공")
    void resetPassword_ShouldReturnOk() throws Exception {
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO("test@example.com", "newPassword123");
        when(validationService.validate(any())).thenReturn(Collections.emptyMap());
        doNothing().when(passwordRecoveryService).resetPassword(any());

        mockMvc.perform(post("/api/password-recovery/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호가 성공적으로 재설정되었습니다."));
    }

    @Test
    @DisplayName("비밀번호 재설정 - 실패 (비밀번호 누락)")
    void resetPassword_ShouldReturnBadRequest() throws Exception {
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO("test@example.com", "");
        when(validationService.validate(any())).thenReturn(Collections.singletonMap("password", "비밀번호를 입력해야 합니다."));

        mockMvc.perform(post("/api/password-recovery/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("비밀번호를 입력해야 합니다."));
    }
}