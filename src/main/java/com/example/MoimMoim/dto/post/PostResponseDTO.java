package com.example.MoimMoim.dto.post;


import com.example.MoimMoim.dto.comment.CommentResponseDTO;
import com.example.MoimMoim.enums.Category;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class PostResponseDTO {

    private Long postId;
    private Long memberId;
    private String title;
    private Category category;
    private String content;
    private String createAt;
    private String nickname;
    private List<CommentResponseDTO> commentList;
    private Long viewCount;

}
