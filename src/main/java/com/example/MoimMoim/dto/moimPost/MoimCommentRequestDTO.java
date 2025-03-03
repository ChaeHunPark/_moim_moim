package com.example.MoimMoim.dto.moimPost;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoimCommentRequestDTO {

    @NotBlank(message = "댓글 내용을 입력하세요.")
    private String content; // 댓글 내용

    private Long memberId;

    private Long postId; // 댓글이 달릴 게시글 ID
}
