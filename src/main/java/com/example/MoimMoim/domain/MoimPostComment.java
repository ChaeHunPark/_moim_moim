package com.example.MoimMoim.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoimPostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moimCommentId;

    @Column(length = 1000)
    private String content;

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;  // 기본값으로 현재 시각 설정

    // 댓글을 조회할 때 회원정보는 함께 조회한다. EAGER.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;  // 댓글 작성자 (회원 정보)

    // 댓글을 조회할 때 게시글 정보를 함께 조회하는건 드물다. LAZY.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_post_id")
    private MoimPost moimPost;
}
