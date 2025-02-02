package com.example.MoimMoim.dto;

import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.CodeVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.RecoveryMethodRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordRecoveryDTOTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();


    /*
     * 1. 비밀번호 찾기, 사용자 인증 성공
     * */


    @Test
    @DisplayName("비밀번호 찾기, 이름, 이메일 입력 유효성 검사 성공")
    void validAccountVerification() {
        // given : dto 생성
        AccountVerificationRequestDTO dto = new AccountVerificationRequestDTO("이름", "test123@naver.com");

        // when : 유효성 검사
        Set<ConstraintViolation<AccountVerificationRequestDTO>> violations = validator.validate(dto);

        // then : 유효성 검사가 실패한 항목이 없으면 테스트 통과
        assertTrue(violations.isEmpty(), "유효성 검사 실패: " + violations);
    }

    /*
     * 1-2. 비밀번호 찾기, 사용자 인증 실패
     * */

    @Test
    @DisplayName("비밀번호 찾기, 이름, 이메일 입력 유효성 검사 실패")
    void invalidAccountVerification() {
        // given : dto 생성
        AccountVerificationRequestDTO dto = new AccountVerificationRequestDTO(
                null,
                null);

        // when : 유효성 검사
        Set<ConstraintViolation<AccountVerificationRequestDTO>> violations = validator.validate(dto);

        // 각 에러 메시지를 검증
        for (ConstraintViolation<AccountVerificationRequestDTO> violation : violations) {
            if ("name".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("이름은 필수 입력 값입니다.").isEqualTo(violation.getMessage());
            }
            if ("email".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("이메일은 필수 입력 값입니다.").isEqualTo(violation.getMessage());
            }
        }
    }


    /*
    * 2. 인증 방법 선택 성공
    * */

    @Test
    @DisplayName("이메일 및 인증 방법 입력 유효성 검사 성공")
    void validRecoveryMethod() {
        // given
        RecoveryMethodRequestDTO dto = new RecoveryMethodRequestDTO("test@example.com", "email");

        // when
        Set<ConstraintViolation<RecoveryMethodRequestDTO>> violations = validator.validate(dto);

        // then
        assertTrue(violations.isEmpty(), "유효성 검사 실패: " + violations);
    }

    /*
    * 2-2. 인증 방법 선택 실패
    * */

    @Test
    @DisplayName("이메일 및 인증 방법 입력 유효성 검사 실패")
    void invalidRecoveryMethod() {
        // given
        RecoveryMethodRequestDTO dto = new RecoveryMethodRequestDTO(null, "phone");

        // when
        Set<ConstraintViolation<RecoveryMethodRequestDTO>> violations = validator.validate(dto);

        // then
        for (ConstraintViolation<RecoveryMethodRequestDTO> violation : violations) {
            if ("email".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("이메일은 필수 입력 값입니다.").isEqualTo(violation.getMessage());
            }
            if ("method".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("인증 방법이 올바르지 않습니다.").isEqualTo(violation.getMessage());
            }
        }
    }


    /*
    * 3.인증 코드 입력 성공
    * */

    @Test
    @DisplayName("이메일 및 인증번호 입력 유효성 검사 성공")
    void validCodeVerification() {
        // given
        CodeVerificationRequestDTO dto = new CodeVerificationRequestDTO("test@example.com", "123456");

        // when
        Set<ConstraintViolation<CodeVerificationRequestDTO>> violations = validator.validate(dto);

        // then
        assertTrue(violations.isEmpty(), "유효성 검사 실패: " + violations);
    }

    /*
    * 3-2. 인증 코드 입력 실패
    * */

    @Test
    @DisplayName("이메일 및 인증번호 입력 유효성 검사 실패")
    void invalidCodeVerification() {
        // given
        CodeVerificationRequestDTO dto = new CodeVerificationRequestDTO(null, "abc123");

        // when
        Set<ConstraintViolation<CodeVerificationRequestDTO>> violations = validator.validate(dto);

        // then
        for (ConstraintViolation<CodeVerificationRequestDTO> violation : violations) {
            if ("email".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("이메일은 필수 입력 값입니다.").isEqualTo(violation.getMessage());
            }
            if ("userInputCode".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("인증번호는 6자리 숫자여야 합니다.").isEqualTo(violation.getMessage());
            }
        }
    }

    /*
    * 4. 비밀번호 변경 성공
    * */

    @Test
    @DisplayName("이메일 및 비밀번호 입력 유효성 검사 성공")
    void validPasswordReset() {
        // given
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO("test@example.com", "Password@123");

        // when
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);

        // then
        assertTrue(violations.isEmpty(), "유효성 검사 실패: " + violations);
    }


    /*
    * 4-2. 비밀번호 변경 실패
    * */
    @Test
    @DisplayName("이메일 및 비밀번호 입력 유효성 검사 실패")
    void invalidPasswordReset() {
        // given
        PasswordResetRequestDTO dto = new PasswordResetRequestDTO(null, "pass");

        // when
        Set<ConstraintViolation<PasswordResetRequestDTO>> violations = validator.validate(dto);

        // then
        for (ConstraintViolation<PasswordResetRequestDTO> violation : violations) {
            if ("email".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("이메일은 필수 입력 값입니다.").isEqualTo(violation.getMessage());
            }
            if ("newPassword".equals(violation.getPropertyPath().toString())) {
                Assertions.assertThat("비밀번호는 8자 이상이어야 하며, 소문자, 대문자, 특수문자를 포함해야 합니다.").isEqualTo(violation.getMessage());
            }
        }
    }


}
