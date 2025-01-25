package com.example.MoimMoim.dto.moim;


import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import lombok.*;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoimPostSummaryResponseDTO {
    private Long postId;
    private String region;
    private String title;
    private Category category;

    private String nickname;

    private int currentParticipants; // 현재 참여 인원
    private int maxParticipants; // 최대 참여 인원
    private MoimStatus moimStatus;
    private String moimDate;

    private String createAt;

    private Long commentCount;
    private Long viewCount;
}
