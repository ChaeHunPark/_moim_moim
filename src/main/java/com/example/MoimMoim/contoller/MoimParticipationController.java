package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.moim.MoimParticipationListResponseDTO;
import com.example.MoimMoim.dto.moim.MoimParticipationRequestDTO;
import com.example.MoimMoim.dto.moim.MoimParticipationResponseDTO;
import com.example.MoimMoim.service.moimService.MoimParticipationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> applyForParticipation(@RequestParam("moimPostId") Long moimPostId,
                                                        @RequestParam("memberId") Long memberId,
                                                        @RequestBody MoimParticipationRequestDTO moimParticipationRequestDTO) {


        moimParticipationService.applyForParticipation(moimPostId, memberId, moimParticipationRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("참여 신청이 완료되었습니다.");
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
        return ResponseEntity.status(HttpStatus.OK).body("참여 신청이 수락되었습니다.");
    }

    // 신청 거절
    @PostMapping("/reject/{participationId}")
    public ResponseEntity<?> rejectParticipation(@PathVariable("participationId") Long participationId,
                                                      @RequestParam("ownerId") Long ownerId,
                                                      @RequestParam("rejectionReason") String rejectionReason) {
        moimParticipationService.rejectParticipation(participationId, ownerId, rejectionReason);
        return  ResponseEntity.status(HttpStatus.OK).body("참여 신청이 거절되었습니다.");
    }

    //수락한 사람 조회
    @GetMapping("/{moimPostId}/accepted-participants")
    public ResponseEntity<List<MoimParticipationListResponseDTO>> getAcceptedParticipants(
            @PathVariable("moimPostId") Long moimPostId) {
        List<MoimParticipationListResponseDTO> acceptedParticipants =
                moimParticipationService.getAcceptedParticipants(moimPostId);
        return ResponseEntity.ok(acceptedParticipants);
    }

    //거절한 사람 조회
    @GetMapping("/{moimPostId}/rejected-participants")
    public ResponseEntity<List<MoimParticipationListResponseDTO>> getRejectedParticipants(
            @PathVariable("moimPostId") Long moimPostId) {
        List<MoimParticipationListResponseDTO> rejectedParticipants =
                moimParticipationService.getRejectedParticipationList(moimPostId);
        return ResponseEntity.ok(rejectedParticipants);
    }
}