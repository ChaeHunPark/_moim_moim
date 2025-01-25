package com.example.MoimMoim.dto.post;

import com.example.MoimMoim.enums.Category;
import lombok.*;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class PostSummaryResponseDTO {

    private Long postId;
    private String title;
    private Category category;
    private String createAt;
    private String nickname;
    private Long commentCount;
    private Long viewCount;
}
