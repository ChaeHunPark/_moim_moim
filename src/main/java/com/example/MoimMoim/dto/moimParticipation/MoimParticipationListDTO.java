package com.example.MoimMoim.dto.moimParticipation;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MoimParticipationListDTO {

    private Long moimParticipationRequestId; // 신청 고유 ID
    private Long moimPostId; // 모임 고유 ID

    // 신청자 정보
    private String nickname; // 신청자 닉네임

    // 신청 상태
    private String participationStatus; // 참여 상태

    // 시간 정보
    private String createdAt; // 신청 생성 시간
}
