package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member")
@Builder
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "birthday", nullable = false)
    @Temporal(TemporalType.DATE)  // 날짜만 저장하고 시각은 무시
    private LocalDate birthday;

    @Column(name = "signup_date", nullable = false, updatable = false)
    private LocalDateTime signupDate;  // 기본값으로 현재 시각 설정


}
