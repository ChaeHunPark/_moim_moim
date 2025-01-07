package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING) // Enum의 이름을 저장
    private Category category;

    @NotBlank
    @Size(max = 10000)
    private String content;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;  // 기본값으로 현재 시각 설정

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;  // 조회수, 초기값 0으로 설정

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 설정
    private Member member;

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }


}
