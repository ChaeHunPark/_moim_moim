package com.example.MoimMoim.dto.moimParticipation;

import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.enums.ParticipationStatus;
import lombok.*;


/*
* 모임 신청 상세 조회 DTO
*
* */


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MoimParticipationResponseDTO {

    private Long moimParticipationRequestId; // 신청 고유 ID
    private Long moimPostId; // 모임 고유 ID

    // 모임 정보
    private String region; // 지역
    private String Category; // 카테고리
    private String moimDate; // 모임 날짜
    private String hostNickname; // 모임 생성자(호스트) 이름

    // 신청자 정보
    private String nickname; // 신청자 닉네임
    private String intro; // 신청자 소개
    private String reasonParticipation; // 참여 사유

    // 신청 상태
    private String moimStatus; // 모임 상태
    private String ParticipationStatus; // 참여 상태

    // 시간 정보
    private String createdAt; // 신청 생성 시간
    private String updatedAt; // 신청 수정 시간


}
