package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.MemberRequestDTO;
import com.example.MoimMoim.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MemberController {

    @Autowired
    MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody MemberRequestDTO memberRequestDTO) {
        // 회원가입 처리 로직
        memberService.signup(memberRequestDTO);
        // 상태 코드 201만 반환
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
