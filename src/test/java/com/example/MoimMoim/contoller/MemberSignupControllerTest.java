package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.config.TestConfig;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;

import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.service.MemberSignupService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any; // any()를 사용하기 위한 import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberSignupController.class) // 컨트롤러만 로드하여 테스트한다.
@Import(TestConfig.class)
class MemberSignupControllerTest {

    //MockBean은 3.4.0부터 deprecated 되었다.
    //MockitoBean을 사용하자
    //@MockitoBean ApplicationContext의 빈을 모킹한다.

    @MockitoBean
    private ValidationService validationService; // Mock으로 생성하여 독립 테스트 가능
    @MockitoBean
    private MemberSignupService memberSignupService; // 실제 로직 실행 없이 가짜 객체로 대체

    @Autowired
    private MockMvc mockMvc; // 내장 서버를 실행하지 않고 컨트롤러를 테스트 할 수 있는 도구;[-
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("유저 회원가입 요청 성공 테스트")
    void signup() throws Exception{

        // given
        MemberSignUpRequestDTO memberSignUpRequestDTO = new MemberSignUpRequestDTO("email@naver.com",
                "Password12@",
                "010-0000-0000",
                "name",
                Gender.MALE,
                "nickname1",
                "1991-01-01");

        // validationService가 항상 빈 에러 리스트를 반환하도록 Mock 설정
        given(validationService.validate(any(BindingResult.class)))
                .willReturn(Collections.emptyMap());

        // when then
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpRequestDTO)))
                        .andExpect(status().isCreated());
    }

    // 유효성 검사 실패 테스트
    @Test
    @DisplayName("유효성 검사 실패 시 400 상태 코드 반환")
    void signupValidationFailTest() throws Exception {
        // 유효성 검사를 실패한 경우를 가정하여 에러 메시지가 설정되는 것을 가정한다.
        Map<String, String> errors = new HashMap<>();
        errors.put("username", "Username is required");
        errors.put("password", "Password must be at least 6 characters");

        given(validationService.validate(any(BindingResult.class))).willReturn(errors);

        // 회원가입 요청 DTO (잘못된 데이터)
        MemberSignUpRequestDTO requestDTO = new MemberSignUpRequestDTO();

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()) // 응답 상태 코드 400 확인
                .andExpect(jsonPath("$.username").value("Username is required")) // username 필드 에러 메시지 검증
                .andExpect(jsonPath("$.password").value("Password must be at least 6 characters")); // password 필드 에러 메시지 검증
    }

//
//
//


}