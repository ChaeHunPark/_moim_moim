package com.example.MoimMoim.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class MoimComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moimCommentId;

    @NotNull
    private String content;

    @NotNull
    private LocalDateTime create_at;

    // 댓글을 조회할 때 회원정보는 함께 조회한다. EAGER.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;  // 댓글 작성자 (회원 정보)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_post_id")
    private MoimPost moimPost;

}
