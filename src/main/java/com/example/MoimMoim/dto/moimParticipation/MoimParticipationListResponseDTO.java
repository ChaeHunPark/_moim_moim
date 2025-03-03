package com.example.MoimMoim.dto.moimParticipation;

import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.ParticipationStatus;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MoimParticipationListResponseDTO {

    private Long moimParticipationRequestId; // 신청 고유 ID
    private Long moimPostId; // 모임 고유 ID

    // 모임 정보
    private String region; // 지역
    private String category; // 카테고리 이름
    private String moimDate; // 모임 날짜

    // 신청자 정보
    private String nickname; // 신청자 닉네임

    // 신청 상태
    private String participationStatus; // 참여 상태

    // 시간 정보
    private String createdAt; // 신청 생성 시간
}
