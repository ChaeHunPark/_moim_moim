package com.example.MoimMoim.dto.passwordrecovery;

/*
* 비밀번호 재설정 DTO
* */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequestDTO {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
            message = "비밀번호는 8자 이상이어야 하며, 소문자, 대문자, 특수문자를 포함해야 합니다."
    )
    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    private String newPassword;
}
