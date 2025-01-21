package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoimPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 ID
    private Long moimPostId;

    @Column(nullable = false, length = 100) // 제목은 필수, 최대 100자
    private String title;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    @Column(nullable = false) // 카테고리는 필수
    private Category category;

    @Column(nullable = false) // 내용은 필수
    private String content;

    @Column(nullable = false) // 지역은 필수
    private String location;

    private String address; // 상세 주소

    private String roadAddress; // 도로명 주소

    private String region; // 행정 구역

    @Column(nullable = false) // 경도는 필수
    private double mapx;

    @Column(nullable = false) // 위도는 필수
    private double mapy;

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    private MoimStatus moimStatus;

    @Column(nullable = false)
    private Long currentParticipants = 1L;

    @Column(nullable = false) // 최대 참가자는 필수
    private Long maxParticipants;

    @Column(nullable = false) // 모임 날짜는 필수
    private LocalDateTime moimDate;

    @Column(nullable = false, updatable = false) // 생성일은 수정 불가
    private LocalDateTime createdAt;

    // 수정 일자
    private LocalDateTime updateAt;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;  // 조회수, 초기값 0으로 설정



    // Member , LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "moimPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimPostComment> moimComments;


}
