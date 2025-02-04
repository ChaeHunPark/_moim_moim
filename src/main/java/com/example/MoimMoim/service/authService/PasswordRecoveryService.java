package com.example.MoimMoim.service.authService;

import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.CodeVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.RecoveryMethodRequestDTO;

public interface PasswordRecoveryService {

    String generateVerificationCode();

    //해당 계정이 존재하는가?
    void isAccountExists(AccountVerificationRequestDTO accountVerificationRequestDTO);

    // 복구 방법 선택 및 인증코드 보내기
    void selectRecoveryMethodAndSendCode(RecoveryMethodRequestDTO recoveryMethodRequestDTO);

    // 인증코드 유효성 검사
    void verifyCode(CodeVerificationRequestDTO codeVerificationRequestDTO);

    // 비밀번호 변경
    void resetPassword(PasswordResetRequestDTO passwordResetRequestDTO);

}
