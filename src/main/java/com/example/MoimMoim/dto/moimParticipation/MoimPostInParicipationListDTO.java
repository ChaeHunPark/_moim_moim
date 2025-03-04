package com.example.MoimMoim.dto.moimParticipation;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoimPostInParicipationListDTO {
    private Long moimPostId;
    private String title;
    private String region;
    private String category;
    private String nickname;
    private int currentParticipants; // 현재 참여 인원
    private int maxParticipants; // 최대 참여 인원
    private String moimStatus;
    private String moimDate;
    private String createAt;

    private List<MoimParticipationListDTO> participationList; // 신청자 리스트


}
