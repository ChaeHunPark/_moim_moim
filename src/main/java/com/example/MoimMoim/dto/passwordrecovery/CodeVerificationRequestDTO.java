package com.example.MoimMoim.dto.passwordrecovery;

/*
* 3. 인증 코드 검증 DTO
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
public class CodeVerificationRequestDTO {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotNull(message = "이메일은 필수 입력 값입니다.")
    private String email;

    // 사용자가 입력한 인증번호
    // 6자리 숫자 입력을 위한 유효성 검사
    @NotNull(message = "인증번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^\\d{6}$", // 6자리 숫자만 허용
            message = "인증번호는 6자리 숫자여야 합니다."
    )
    private String userInputCode;
}
