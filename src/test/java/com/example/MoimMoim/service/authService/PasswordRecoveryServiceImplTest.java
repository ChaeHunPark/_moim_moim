package com.example.MoimMoim.service.authService;


import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.exception.auth.TooManyRequestsException;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private VerificationCodeManager verificationCodeManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    PasswordRecoveryServiceImpl passwordRecoveryService;


    @Test
    @DisplayName("이메일로 인증번호 전송 성공")
    void sendEmail_Success() {
        // given
        String email = "test@example.com";
        Mockito.when(verificationCodeManager.canRequestNewCode(email)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> passwordRecoveryService.sendEmail(email));
    }

    @Test
    @DisplayName("너무 자주 인증번호 요청시 예외 발생")
    void sendEmail_TooManyRequests() {
        // given
        String email = "test@example.com";
        Mockito.when(verificationCodeManager.canRequestNewCode(email)).thenReturn(false);

        // when & then
        assertThrows(TooManyRequestsException.class,
                () -> passwordRecoveryService.sendEmail(email));
    }

    @Test
    @DisplayName("회원 존재 여부 검증 - 회원 존재")
    void isAccountExists_Success() {
        // given
        AccountVerificationRequestDTO request = new AccountVerificationRequestDTO(
                "홍길동",
                "test@example.com");
        Mockito.when(memberRepository.existsByNameAndEmail(request.getName(), request.getEmail()))
                .thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> passwordRecoveryService.isAccountExists(request));
    }

    @Test
    @DisplayName("회원 존재 여부 검증 - 회원 없음")
    void isAccountExists_Failure() {
        // given
        AccountVerificationRequestDTO request = new AccountVerificationRequestDTO("홍길동", "test@example.com");
        Mockito.when(memberRepository.existsByNameAndEmail(request.getName(), request.getEmail()))
                .thenReturn(false);

        // when & then
        assertThrows(MemberNotFoundException.class, () -> passwordRecoveryService.isAccountExists(request));
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPassword_Success() {
        // given
        String email = "test@example.com";
        String newPassword = "NewPassword@123";

        PasswordResetRequestDTO request = new PasswordResetRequestDTO(email, newPassword);

        Member member = Member.builder()
                .email(email)
                .password("OldPassword@123")
                .build();

        // 이메일 반환
        Mockito.when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        // 새 비밀번호 설정
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn("EncodedPassword");


        // when
        passwordRecoveryService.resetPassword(request);


        // then
        assertThat(member.getPassword()).isEqualTo("EncodedPassword");
    }

    @Test
    @DisplayName("비밀번호 재설정 - 회원 없음 예외")
    void resetPassword_Failure_MemberNotFound() {
        // given
        PasswordResetRequestDTO request = new PasswordResetRequestDTO("test@example.com", "NewPassword@123");
        Mockito.when(memberRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class, () -> passwordRecoveryService.resetPassword(request));
    }




}