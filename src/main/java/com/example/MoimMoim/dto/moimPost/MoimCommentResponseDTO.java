package com.example.MoimMoim.dto.moimPost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoimCommentResponseDTO {
    private Long commentId;
    private Long memberId;
    private String content;
    private String nickname;
    private String createAt;
}
