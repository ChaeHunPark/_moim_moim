package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.dto.moimParticipation.MoimParticipationListDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationRequestDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationResponseDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimPostInParicipationListDTO;

import java.util.List;

public interface MoimParticipationService {

    // 1. 참여 신청

    void applyForParticipation(Long moimPostId,
                               Long memberId,
                               MoimParticipationRequestDTO moimParticipationRequestDTO);

    // 2. 단일 조회 (신청한 조회)
    MoimParticipationResponseDTO getMyParticipation(Long memberId, Long participationId);

    // 2-1. 단일 조회 (신청받은 조회)
    MoimParticipationResponseDTO getReceivedParticipation(Long ownerId, Long participationId);

    // 3. 리스트 조회 (신청한 목록)
    List<MoimPostInParicipationListDTO> getApplicantParticipationList(Long applicantId);

    // 3-1. 리스트 조회 (신청받은 목록)
    List<MoimPostInParicipationListDTO> getReceivedParticipationList(Long receiverId);

    // 4.신청 수락
    void acceptParticipation(Long participationId, Long ownerId);

    // 4-1.신청 거절
    void rejectParticipation(Long participationId, Long ownerId, String rejectionReason);

    // 5.수락한 사람 조회
    List<MoimParticipationListDTO> getAcceptedParticipants(Long moimPostId);

    // 5-1.거절한 사람 조회
    List<MoimParticipationListDTO> getRejectedParticipationList(Long moimPostId);


    //모임 취소
    void cancellationMoimPost(Long moimPostId, String reason);

}
