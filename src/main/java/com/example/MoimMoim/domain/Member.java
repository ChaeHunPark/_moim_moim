package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "birthday", nullable = false)
    @Temporal(TemporalType.DATE)  // 날짜만 저장하고 시각은 무시
    private LocalDate birthday;

    @Column(name = "signup_date", nullable = false, updatable = false)
    private LocalDateTime signupDate;  // 기본값으로 현재 시각 설정

    @ManyToOne(fetch = FetchType.EAGER) // LAZY 로딩
    @JoinColumn(name = "role_id", nullable = false) // Role 외래키
    private Role role;

    // 멤버를 조회할 때 댓글을 함께 조회하는 경우는 드물다. lAZY
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Comment> comments;  // 회원이 작성한 댓글들

    // 멤버를 조회할 때 게시글을 함께 조회하는 경우는 드물다. lAZY
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimPostComment> moimComments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimPost> moimPosts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimAccptedMember> moimAccptedMembers;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimParticipation> moimParticipations;


}
