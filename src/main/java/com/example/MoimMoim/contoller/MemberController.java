package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.AccountVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.CodeVerificationRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.PasswordResetRequestDTO;
import com.example.MoimMoim.dto.passwordrecovery.RecoveryMethodRequestDTO;
import com.example.MoimMoim.service.MemberSignupService;
import com.example.MoimMoim.service.PasswordRecoveryService;
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
@RequestMapping("/api")
public class MemberController {


    private final ValidationService validationService;
    private final MemberSignupService memberSignupService;
    private final PasswordRecoveryService passwordRecoveryService;

    @Autowired
    public MemberController(ValidationService validationService, MemberSignupService memberSignupService, PasswordRecoveryService passwordRecoveryService) {
        this.validationService = validationService;
        this.memberSignupService = memberSignupService;
        this.passwordRecoveryService = passwordRecoveryService;
    }


    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody MemberSignUpRequestDTO memberSignUpRequestDTO, BindingResult bindingResult) {
        // 유효성 검증 실패 처리
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

//        try {
//            // 회원가입 처리 로직
//            memberSignupService.signup(memberSignUpRequestDTO);
//            return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful.");
//        } catch (IllegalStateException e) {
//            // 비즈니스 로직에서 발생한 예외 처리
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원이미 존재");
//        }

        // 회원가입 처리 로직, GlobalExceptionHandler 활용으로 try-catch문이 제거 가능해졌다.
        memberSignupService.signup(memberSignUpRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");

    }

    // 비밀번호 찾기
    // 1. 계정 존재 여부 확인
    @PostMapping("/password-recovery/start")
    public ResponseEntity<?> checkAccountExistence(@Valid @RequestBody AccountVerificationRequestDTO requestDTO, BindingResult bindingResult) {

        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        //회원이 존재하는지 조회한다.
        passwordRecoveryService.isAccountExists(requestDTO);
        return ResponseEntity.ok("계정이 존재합니다. 다음 단계로 진행합니다.");
    }


    // 비밀번호 찾기
    // 2. 인증 방법 선택 (이메일 외 다른방법 추가예정)
    @PostMapping("/password-recovery/choose-method")
    public ResponseEntity<?> chooseRecoveryMethod(@Valid @RequestBody RecoveryMethodRequestDTO requestDTO, BindingResult bindingResult) {

        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 인증방법 선택 및 인증코드 전송
        passwordRecoveryService.selectRecoveryMethodAndSendCode(requestDTO);
        return ResponseEntity.ok("인증 코드가 전송되었습니다.");
    }

    // 3. 비밀번호 찾기 - 인증 코드 검증
    @PostMapping("/password-recovery/verify-code")
    public ResponseEntity<?> verifyRecoveryCode(@RequestBody CodeVerificationRequestDTO requestDTO, BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }


        // 인증번호 제출하기
        passwordRecoveryService.verifyCode(requestDTO);
        return ResponseEntity.ok("인증 코드가 확인되었습니다. 비밀번호를 재설정해주세요.");
    }

    // 4. 비밀번호 재설정
    @PostMapping("/password-recovery/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestDTO requestDTO, BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        // 비밀번호 변경
        passwordRecoveryService.resetPassword(requestDTO);
        return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
    }
}


