package com.example.MoimMoim.domain;

import com.example.MoimMoim.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    @Column(columnDefinition = "TEXT") //HTML 태그 저장 허용
    private String content;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;  // 기본값으로 현재 시각 설정

    // 수정 일자
    private LocalDateTime updateAt;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;  // 조회수, 초기값 0으로 설정

    @ManyToOne(fetch = FetchType.LAZY) // 연관관계 주인
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 설정
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }


    public void update(String title, Category category, String Content) {
        this.title = title;
        this.category = category;
        this.content = content;
        setUpdateAt(LocalDateTime.now());
    }

}
