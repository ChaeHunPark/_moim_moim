package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.moimParticipation.MoimParticipationListResponseDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationRequestDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationResponseDTO;
import com.example.MoimMoim.jwtUtil.CustomUserDetails;
import com.example.MoimMoim.service.moimService.MoimParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participation")
public class MoimParticipationController {

    private final MoimParticipationService moimParticipationService;

    @Autowired
    public MoimParticipationController(MoimParticipationService moimParticipationService) {
        this.moimParticipationService = moimParticipationService;
    }

    // 1. 참여 신청
    @PostMapping("/apply")
    public ResponseEntity<?> applyForParticipation(@RequestParam("moimPostId") Long moimPostId,
                                                        @RequestParam("memberId") Long memberId,
                                                        @RequestBody MoimParticipationRequestDTO moimParticipationRequestDTO) {


        moimParticipationService.applyForParticipation(moimPostId, memberId, moimParticipationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","참여 신청이 완료되었습니다."));
    }

    // 2. 단일 조회 (신청한 조회)
    @GetMapping("/my/{participationId}")
    public ResponseEntity<?> getMyParticipation(@PathVariable("participationId") Long participationId,
                                                                           @RequestParam("memberId") Long memberId) {
        MoimParticipationResponseDTO responseDTO = moimParticipationService.getMyParticipation(memberId, participationId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    // 2-1. 단일 조회 (신청받은 조회)
    @GetMapping("/received/{participationId}")
    public ResponseEntity<?> getReceivedParticipation(@PathVariable("participationId") Long participationId,
                                                                                 @RequestParam("ownerId") Long ownerId) {
        MoimParticipationResponseDTO responseDTO = moimParticipationService.getReceivedParticipation(ownerId, participationId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    // 신청한 목록 조회
    @GetMapping("/my-participation/{memberId}")
    public ResponseEntity<List<MoimParticipationListResponseDTO>> getMyParticipationList(@PathVariable("memberId") Long memberId) {
        List<MoimParticipationListResponseDTO> participationList = moimParticipationService.getMyParticipationList(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(participationList);
    }

    // 신청받은 목록 조회 (모임 주최자)
    @GetMapping("/received-participation/{ownerId}")
    public ResponseEntity<List<MoimParticipationListResponseDTO>> getReceivedParticipationList(@PathVariable("ownerId") Long ownerId) {
        List<MoimParticipationListResponseDTO> participationList = moimParticipationService.getReceivedParticipationList(ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(participationList);
    }

    // 신청 수락
    @PostMapping("/accept/{participationId}")
    public ResponseEntity<?> acceptParticipation(@PathVariable("participationId") Long participationId, @RequestParam("ownerId") Long ownerId) {
        moimParticipationService.acceptParticipation(participationId, ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","참여 신청이 수락되었습니다."));
    }

    // 신청 거절
    @PostMapping("/reject/{participationId}")
    public ResponseEntity<?> rejectParticipation(@PathVariable("participationId") Long participationId,
                                                      @RequestParam("ownerId") Long ownerId,
                                                      @RequestParam("rejectionReason") String rejectionReason) {
        moimParticipationService.rejectParticipation(participationId, ownerId, rejectionReason);
        return  ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "참여 신청이 거절되었습니다."));
    }

    //수락한 사람 조회
    @GetMapping("/accepted-participants/{moimPostId}")
    public ResponseEntity<List<MoimParticipationListResponseDTO>> getAcceptedParticipants(
            @PathVariable("moimPostId") Long moimPostId) {
        List<MoimParticipationListResponseDTO> acceptedParticipants =
                moimParticipationService.getAcceptedParticipants(moimPostId);
        return ResponseEntity.ok(acceptedParticipants);
    }

    //거절한 사람 조회
    @GetMapping("/rejected-participants/{moimPostId}")
    public ResponseEntity<List<MoimParticipationListResponseDTO>> getRejectedParticipants(
            @PathVariable("moimPostId") Long moimPostId) {
        List<MoimParticipationListResponseDTO> rejectedParticipants =
                moimParticipationService.getRejectedParticipationList(moimPostId);
        return ResponseEntity.ok(rejectedParticipants);
    }

    // 모임 취소 -> 수락목록 회원 삭제
    @DeleteMapping("/cancellation/{moimPostId}")
    public ResponseEntity<?> cancelMoimPost(
            @PathVariable("moimPostId") Long moimPostId,
            @RequestParam("reason") String reason) {
        // 모임 게시글 취소 처리
        moimParticipationService.cancellationMoimPost(moimPostId, reason);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "모임 취소가 완료되었습니다."));
    }
}