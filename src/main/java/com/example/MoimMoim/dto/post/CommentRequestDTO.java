package com.example.MoimMoim.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {

    @NotNull(message = "댓글 내용을 입력하세요.")
    private String content; // 댓글 내용

    @NotNull(message = "작성자 ID는 필수 항목입니다.")
    private Long memberId;

    @NotNull(message = "게시글 ID는 필수 항목입니다.")
    private Long postId; // 댓글이 달릴 게시글 ID
}
