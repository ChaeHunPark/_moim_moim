package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.MemberRequestDTO;
import com.example.MoimMoim.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody MemberRequestDTO memberRequestDTO, BindingResult bindingResult) {
        // 유효성 검증 실패 처리
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            // 유효성 검증 에러 메시지 반환
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // 회원가입 처리 로직
            memberService.signup(memberRequestDTO);
            // 상태 코드 201만 반환
            return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful.");
        } catch (IllegalStateException e) {
            // 비즈니스 로직에서 발생한 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
