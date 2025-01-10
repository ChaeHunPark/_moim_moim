package com.example.MoimMoim.domain;

import jakarta.persistence.*;
import lombok.*;

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

    private String addressTitle;

    private String addressCategory;

    private String roadAddress;

    private Double mapx; // 경도

    private Double mapy; // 위도

    @OneToOne
    @JoinColumn(name = "post_id") // 외래키
    private Post post;

}
