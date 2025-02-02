package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.CodeVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.RecoveryMethodRequestDTO;
import com.example.MoimMoim.service.authService.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/password-recovery")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;
    private final ValidationService validationService;

    @Autowired
    public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService, ValidationService validationService) {
        this.passwordRecoveryService = passwordRecoveryService;
        this.validationService = validationService;
    }

    // 비밀번호 찾기
    // 1. 계정 존재 여부 확인
    @PostMapping("/start")
    public ResponseEntity<?> checkAccountExistence(@Valid @RequestBody AccountVerificationRequestDTO requestDTO, BindingResult bindingResult) {

        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        //회원이 존재하는지 조회한다.
        passwordRecoveryService.isAccountExists(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("계정이 존재합니다. 다음 단계로 진행합니다.");
    }


    // 비밀번호 찾기
    // 2. 인증 방법 선택 (이메일 외 다른방법 추가예정)
    @PostMapping("/choose-method")
    public ResponseEntity<?> chooseRecoveryMethod(@Valid @RequestBody RecoveryMethodRequestDTO requestDTO, BindingResult bindingResult) {

        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 인증방법 선택 및 인증코드 전송
        passwordRecoveryService.selectRecoveryMethodAndSendCode(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("인증 코드가 전송되었습니다.");
    }

    // 3. 비밀번호 찾기 - 인증 코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyRecoveryCode(@RequestBody CodeVerificationRequestDTO requestDTO, BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }


        // 인증번호 제출하기
        passwordRecoveryService.verifyCode(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("인증 코드가 확인되었습니다. 비밀번호를 재설정해주세요.");
    }

    // 4. 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestDTO requestDTO, BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 비밀번호 변경
        passwordRecoveryService.resetPassword(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 성공적으로 재설정되었습니다.");
    }
}
