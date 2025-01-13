package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class MoimPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moimPostId;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING) // Enum의 이름을 저장
    private Category category;

    @NotBlank
    @Size(max = 3000)
    @Column(columnDefinition = "TEXT") //HTML 태그 저장 허용
    private String content;

    @NotNull
    private String location;

    @NotNull
    private String address;

    @NotNull
    private String roadAddress;

    @NotNull
    private String region;

    @NotNull
    private Double mapy;

    @NotNull
    private Double mapx;

    @NotNull
    @Enumerated(EnumType.STRING) // Enum의 이름을 저장
    private MoimStatus moimStatus;

    @NotNull
    private Long viewCount = 0L;

    // 최대 인원
    @NotNull
    private int maxParticipants;

    // 현재 인원, 모임 주체자 1인 포함
    @NotNull
    private int currentParticipants = 1;

    // 모임 날짜
    @NotNull
    private LocalDateTime moimDate;

    @NotNull
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 설정
    private Member member;

    @OneToMany(mappedBy = "moimPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MoimComment> comments;


    public void incrementViewCount() {
        this.viewCount++;
    }
}
