package com.example.MoimMoim.dto.moimPost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoimCommentResponseDTO {
    private String content;
    private String nickname;
    private String createAt;
}
