package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.enums.Gender;
import com.example.MoimMoim.service.MemberSignupService;
import com.example.MoimMoim.service.PasswordRecoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest는 실제 Security 설정을 로드 하지 않기 때문에
// 밑과 같은 어노테이션을 사용하여 테스트 환경을 설정함
@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    //MockBean은 3.4.0부터 deprecated 되었다.
    //MockitoBean을 사용하자
    @MockitoBean
    private MemberSignupService memberSignupService;

    @Autowired
    MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 회원가입 요청 테스트")
    void signup() throws Exception{

        // given
        MemberSignUpRequestDTO memberSignUpRequestDTO = new MemberSignUpRequestDTO("email@naver.com", "Password12@", "010-0000-0000", "name", Gender.MALE, "nickname1","1991-01-01");

        // when then
        mockmvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberSignUpRequestDTO)))
                        .andExpect(status().isCreated());
    }




}