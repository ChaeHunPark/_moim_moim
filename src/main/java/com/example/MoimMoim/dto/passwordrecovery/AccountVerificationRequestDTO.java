package com.example.MoimMoim.dto.passwordrecovery;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* 1. 계정확인 DTO
* */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerificationRequestDTO {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

}
