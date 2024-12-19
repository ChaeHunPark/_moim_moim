package com.example.MoimMoim.dto;

import com.example.MoimMoim.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private String email;
    private String password;
    private String phone;
    private String name;
    private Gender gender;
    private String nickname;
    private LocalDate birthday;
}
