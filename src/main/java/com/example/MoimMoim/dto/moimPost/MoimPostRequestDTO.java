package com.example.MoimMoim.dto.moimPost;


import com.example.MoimMoim.dto.post.PostRequestDTO;
import lombok.*;



@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoimPostRequestDTO {
    private String addressTitle;
    private String addressCategory;
    private String roadAddress;
    private Double mapx; // 경도
    private Double mapy; // 위도
    private PostRequestDTO postRequestDTO;
}
