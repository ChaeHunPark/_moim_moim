package com.example.MoimMoim.dto.passwordrecovery;

/*
 * 2.복구 방법 선택 DTO, 카카오 or 이메일
 * */

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class RecoveryMethodRequestDTO {
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @NotNull(message = "인증 방법을 선택해야 합니다.")
    @Pattern(
            regexp = "^(email)$",
            message = "인증 방법이 올바르지 않습니다."
    )

    private String method;
}
