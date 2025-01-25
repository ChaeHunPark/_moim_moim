package com.example.MoimMoim.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력하세요.")
    private String content; // 댓글 내용

    private Long memberId;

    private Long postId; // 댓글이 달릴 게시글 ID
}
