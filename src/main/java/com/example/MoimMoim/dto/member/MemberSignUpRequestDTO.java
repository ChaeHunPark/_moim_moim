package com.example.MoimMoim.dto.member;

import com.example.MoimMoim.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpRequestDTO {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$",
            message = "비밀번호는 8자 이상이어야 하며, 소문자, 대문자, 특수문자를 포함해야 합니다."
    )
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @Pattern(
            regexp = "^(\\d{3}-\\d{3,4}-\\d{4})$",
            message = "전화번호 형식이 올바르지 않습니다."
    )
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 1, max = 50, message = "이름은 1자 이상 50자 이하로 입력해야 합니다.")
    private String name;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private Gender gender;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, message = "닉네임은 2자 이상이어야 합니다.")
    private String nickname;

    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "생년월일은 YYYY-MM-DD 형식이어야 합니다."
    )
    private String birthday;

}