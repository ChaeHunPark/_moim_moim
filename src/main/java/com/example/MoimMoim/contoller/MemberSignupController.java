package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.member.MemberSignUpRequestDTO;
import com.example.MoimMoim.service.MemberSignupService;

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
public class MemberSignupController {

    private final ValidationService validationService;
    private final MemberSignupService memberSignupService;

    @Autowired
    public MemberSignupController(ValidationService validationService, MemberSignupService memberSignupService) {
        this.validationService = validationService;
        this.memberSignupService = memberSignupService;
    }


    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody MemberSignUpRequestDTO memberSignUpRequestDTO, BindingResult bindingResult) {
        // 유효성 검증 실패 처리
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        memberSignupService.signup(memberSignUpRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");

    }

}


