package com.example.MoimMoim.service.authService;

import com.example.MoimMoim.exception.auth.VerificationCodeExpiredException;
import com.example.MoimMoim.exception.auth.VerificationCodeMismatchException;
import com.example.MoimMoim.exception.auth.VerificationCodeNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VerificationCodeManagerImplTest {
    private VerificationCodeManagerImpl verificationCodeManager;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";

    @BeforeEach
    void setUp() {
        verificationCodeManager = new VerificationCodeManagerImpl();
    }

    @Test
    @DisplayName("인증번호 저장 및 조회 성공")
    void saveVerificationCode_Success() {
        // when
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // then
        // 인증번호가 존재하면 False 반환하기에 검증 완료
        assertThat(verificationCodeManager.canRequestNewCode(TEST_EMAIL)).isFalse();

    }

    @Test
    @DisplayName("유효한 인증번호 검증 성공")
    void validateVerificationCode_Success() {
        // given
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // when & then (예외가 발생하지 않으면 성공)
        verificationCodeManager.validateVerificationCode(TEST_EMAIL, TEST_CODE);
    }

    @Test
    @DisplayName("만료된 인증번호 검증 실패")
    void validateVerificationCode_Expired() throws InterruptedException {
        // given
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // 5분 후 인증번호가 만료됨 (유효시간: 5분)
        TimeUnit.MINUTES.sleep(5);

        // when & then
        assertThatThrownBy(() ->
                verificationCodeManager.validateVerificationCode(TEST_EMAIL, TEST_CODE))
                .isInstanceOf(VerificationCodeExpiredException.class)
                .hasMessage("인증정보가 만료되었습니다.");
    }

    @Test
    @DisplayName("잘못된 인증번호 검증 실패")
    void validateVerificationCode_WrongCode() {
        // given
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // when & then
        assertThatThrownBy(() -> verificationCodeManager.validateVerificationCode(TEST_EMAIL, "654321"))
                .isInstanceOf(VerificationCodeMismatchException.class)
                .hasMessage("인증번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("인증번호가 없을 때 검증 실패")
    void validateVerificationCode_NotFound() {
        // when & then

        assertThatThrownBy(() -> verificationCodeManager.validateVerificationCode(TEST_EMAIL, TEST_CODE))
                .isInstanceOf(VerificationCodeNotFoundException.class)
                .hasMessage("인증정보가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("인증번호 재요청 가능 여부 - 재요청 불가")
    void canRequestNewCode_False() {
        // given
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // when
        boolean canRequest = verificationCodeManager.canRequestNewCode(TEST_EMAIL);

        // then
        assertThat(canRequest).isFalse();
    }

    @Test
    @DisplayName("인증번호 재요청 가능 여부 - 재요청 가능")
    void canRequestNewCode_True() throws InterruptedException {
        // given
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // 1분 후 재요청 가능
        TimeUnit.MINUTES.sleep(1);

        // when
        boolean canRequest = verificationCodeManager.canRequestNewCode(TEST_EMAIL);

        // then
        assertThat(canRequest).isTrue();
    }

    @Test
    @DisplayName("인증번호 삭제 후 검증 실패")
    void removeVerificationCode_Success() {
        // given
        verificationCodeManager.saveVerificationCode(TEST_EMAIL, TEST_CODE);

        // when
        verificationCodeManager.removeVerificationCode(TEST_EMAIL);

        // 인증 번호가 삭제 되었는지 재확인
        assertThatThrownBy(() -> verificationCodeManager.validateVerificationCode(TEST_EMAIL, TEST_CODE))
                .isInstanceOf(VerificationCodeNotFoundException.class);
    }
}