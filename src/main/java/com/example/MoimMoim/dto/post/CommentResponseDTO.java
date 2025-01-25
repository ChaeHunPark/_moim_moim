package com.example.MoimMoim.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class CommentResponseDTO {
    private Long commentId;
    private Long memberId;
    private String content;
    private String nickname;
    private String createAt;

}
