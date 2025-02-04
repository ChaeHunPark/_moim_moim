package com.example.MoimMoim.integration.PasswordRecoveryTest;


import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.service.authService.PasswordRecoveryServiceImpl;
import com.example.MoimMoim.service.authService.VerificationCodeManager;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PasswordRecoveryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    String email = "cogns8571@naver.com";
    String name = "홍길동";
    String password = "QWer123@";
    String newPassword = "Test123@";

    final String CODE = "123456";

    @MockitoSpyBean // 실제 객체를 가져오지만, 가상 응답 만들수 있음!
    private PasswordRecoveryServiceImpl passwordRecoveryService;

    @MockitoSpyBean
    private VerificationCodeManager verificationCodeManager;

    @MockitoSpyBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @BeforeEach
    void setup() throws Exception {
        //given
        String requestBody = """
                {
                    "email": "%s",
                    "password": "%s",
                    "phone": "010-1234-5678",
                    "name": "%s",
                    "gender": "MALE",
                    "nickname": "길동이",
                    "birthday": "1995-08-15"
                }
                """.formatted(email, password, name);


        //when
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("회원가입이 완료되었습니다."));
    }


    @Test
    @DisplayName("비밀번호 찾기 및 변경 성공 테스트")
    void passwordRecoveryIntegrationTest () throws Exception {
        String requestBody_start = """
                {
                    "name": "%s",
                    "email": "%s"
                }
                """.formatted(name, email);

        String requestBody_chooseMethod = """
                {
                    "email" : "%s",
                    "method" : "email"
                }
                """.formatted(email);

        String requestBody_verifyCode = """
                {
                    "email" : "%s",
                    "userInputCode" : "%s"
                }
                """.formatted(email, CODE);

        String requestBody_resetPassword = """
                {
                    "email" : "cogns8571@naver.com",
                    "newPassword" : "%s"
                }
                
                """.formatted(email, newPassword);




        // 1. 계정 존재 여부 확인
        mockMvc.perform(post("/api/password-recovery/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody_start))
                .andExpect(status().isOk())
                .andExpect(content().string("계정이 존재합니다. 다음 단계로 진행합니다."));


        // 실제 메일은 전송하지 않도록 한다. (실행 시간이 오래걸린다.)
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // 인증 코드 전송은 123456으로 전송한다.
        when(passwordRecoveryService.generateVerificationCode()).thenReturn(CODE);


        // 2. 인증 방법 선택
        mockMvc.perform(post("/api/password-recovery/choose-method")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody_chooseMethod))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 전송되었습니다."));

        // 3. 인증 코드 검증
        mockMvc.perform(post("/api/password-recovery/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody_verifyCode))
                .andExpect(status().isOk())
                .andExpect(content().string("인증 코드가 확인되었습니다. 비밀번호를 재설정해주세요."));



        // 4. 비밀번호 변경
        mockMvc.perform(post("/api/password-recovery/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody_resetPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호가 성공적으로 재설정되었습니다."));
        
        // 비밀번호 바뀌었는지 검증
       Member member = memberRepository.findByEmail(email).
                orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        String newPassword = member.getPassword();


        assertThat(passwordEncoder.matches(password, member.getPassword())).isFalse(); // 이전 비밀번호와 비교
        assertThat(passwordEncoder.matches(newPassword, member.getPassword())).isFalse(); // 바뀐 비밀번호와 비교


    }






}
