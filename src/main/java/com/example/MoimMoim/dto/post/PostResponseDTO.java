package com.example.MoimMoim.dto.post;

import com.example.MoimMoim.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class PostResponseDTO {

    private Long postId;
    private String title;
    private Category category;
    private String content;
    private String createAt;
    private String nickname;
    private Long viewCount;

}
