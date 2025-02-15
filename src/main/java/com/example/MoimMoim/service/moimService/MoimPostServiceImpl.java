package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.domain.*;
import com.example.MoimMoim.dto.moim.MoimCommentResponseDTO;
import com.example.MoimMoim.dto.moim.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moim.MoimPostResponseDTO;
import com.example.MoimMoim.dto.moim.MoimPostSummaryResponseDTO;
import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.enums.ParticipationStatus;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimAccptedMemberRepository;
import com.example.MoimMoim.repository.MoimParticipationRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.example.MoimMoim.service.utilService.DateTimeUtilService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MoimPostServiceImpl implements MoimPostService{

    private final MemberRepository memberRepository;
    private final DateTimeUtilService dateTimeUtilService;
    private final MoimPostRepository moimPostRepository;
    private final MoimParticipationRepository moimParticipationRepository;
    private final MoimAccptedMemberRepository moimAccptedMemberRepository;

    @Autowired
    public MoimPostServiceImpl(MemberRepository memberRepository, DateTimeUtilService dateTimeUtilService, MoimPostRepository moimPostRepository, MoimParticipationRepository moimParticipationRepository, JPAQueryFactory jpaQueryFactory, MoimAccptedMemberRepository moimAccptedMemberRepository) {
        this.memberRepository = memberRepository;
        this.dateTimeUtilService = dateTimeUtilService;
        this.moimPostRepository = moimPostRepository;
        this.moimParticipationRepository = moimParticipationRepository;
        this.moimAccptedMemberRepository = moimAccptedMemberRepository;
    }

    private String extractRegionFromData(String address){
        return address.split(" ")[0];
    }

    private Member findMember (Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));
    }



    private MoimPost convertMoimPost(MoimPostRequestDTO moimPostRequestDTO){
        return MoimPost.builder()
                .member(findMember(moimPostRequestDTO.getMemberId()))
                .title(moimPostRequestDTO.getTitle())
                .category(moimPostRequestDTO.getCategory())
                .content(moimPostRequestDTO.getContent())
                .location(moimPostRequestDTO.getLocation())
                .address(moimPostRequestDTO.getAddress())
                .region(extractRegionFromData(moimPostRequestDTO.getAddress()))
                .roadAddress(moimPostRequestDTO.getRoadAddress())
                .mapx(moimPostRequestDTO.getMapx())
                .mapy(moimPostRequestDTO.getMapy())
                .maxParticipants(moimPostRequestDTO.getMaxParticipants())
                .currentParticipants(1)
                .moimDate(moimPostRequestDTO.getMoimDate())
                .moimStatus(MoimStatus.RECRUITING)
                .createdAt(LocalDateTime.now())
                .updateAt(null)
                .viewCount(0L)
                .cancellationReason(null)
                .build();
    }


    @Transactional
    @Override
    public void createMoimPost(MoimPostRequestDTO moimPostRequestDTO) {
        MoimPost moimPost = convertMoimPost(moimPostRequestDTO);
        moimPostRepository.save(moimPost);
    }

    @Transactional
    @Override
    public MoimPostResponseDTO viewMoimPost(Long postId) {
        MoimPost moimPost = moimPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        moimPost.incrementViewCount();
        moimPostRepository.save(moimPost);

        List<MoimCommentResponseDTO> comments = new ArrayList<>();

        if (moimPost.getMoimComments() != null ) {
            comments = moimPost.getMoimComments().stream()
                .map(comment -> new MoimCommentResponseDTO(
                        comment.getMoimCommentId(),
                        comment.getMember().getMemberId(),
                        comment.getContent(),
                        comment.getMember().getNickname(),
                        dateTimeUtilService.formatForClient(comment.getCreateAt())
                )).collect(Collectors.toList());
        }



        MoimPostResponseDTO moimPostResponseDTO = MoimPostResponseDTO.builder()
                .memberId(moimPost.getMember().getMemberId())
                .moimPostId(moimPost.getMoimPostId())
                .title(moimPost.getTitle())
                .content(moimPost.getContent())
                .location(moimPost.getLocation())
                .address(moimPost.getAddress())
                .roadAddress(moimPost.getRoadAddress())
                .region(moimPost.getRegion())
                .mapx(moimPost.getMapx())
                .mapy(moimPost.getMapy())
                .currentParticipants(moimPost.getCurrentParticipants())
                .maxParticipants(moimPost.getMaxParticipants())
                .category(moimPost.getCategory())
                .moimStatus(moimPost.getMoimStatus())
                .viewCount(moimPost.getViewCount())
                .moimDate(dateTimeUtilService.formatForClient(moimPost.getMoimDate()))
                .createdAt(dateTimeUtilService.formatForClient(moimPost.getCreatedAt()))
                .moimCommentList(comments)
                .build();

        // 업데이트가 null 일수도 있다.
        if(moimPost.getUpdateAt() != null){
            moimPostResponseDTO.setUpdateAt(dateTimeUtilService.formatForClient(moimPost.getUpdateAt()));
        }



        return moimPostResponseDTO;


    }

    @Override
    public List<MoimPostSummaryResponseDTO> getPostList(
            String category, String sortBy, String keyword, String searchBy,
            String region, String moimStatus, int page, int size) {

        Pageable pageable = createPageable(page - 1, size);

        return moimPostRepository.getPostList(category, sortBy, pageable, keyword, searchBy, region, moimStatus);
    }

    @Transactional
    @Override
    public void updatePost(Long moimPostId, MoimPostRequestDTO requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        MoimPost moimPost = moimPostRepository.findByMoimPostIdAndMember(moimPostId, member)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

        moimPost.setTitle(requestDTO.getTitle());
        moimPost.setContent(requestDTO.getContent());
        moimPost.setLocation(requestDTO.getLocation());
        moimPost.setAddress(requestDTO.getAddress());
        moimPost.setRoadAddress(requestDTO.getRoadAddress());
        moimPost.setRegion(extractRegionFromData(requestDTO.getRoadAddress()));
        moimPost.setMapx(requestDTO.getMapx());
        moimPost.setMapy(requestDTO.getMapy());
        moimPost.setCategory(requestDTO.getCategory());
        moimPost.setMoimDate(requestDTO.getMoimDate());
        moimPost.setUpdateAt(LocalDateTime.now());

        moimPostRepository.save(moimPost);
    }

    @Override
    public void cancellationMoimPost(Long moimPostId, String reason) {

        MoimPost moimPost = moimPostRepository.findById(moimPostId)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

        // 1. 포스트 취소 상태로 변경
        moimPost.setMoimStatus(MoimStatus.CANCELED);
        moimPost.setCancellationReason(reason);

        moimPostRepository.save(moimPost);

        // 2. 모임 신청자들의 상태 변경
        List<MoimParticipation> participationList = moimParticipationRepository.findByMoimPost(moimPost);

        // 거절한 사람은 상태 변경이 필요없음.
        for (MoimParticipation participation : participationList) {
            if(participation.getParticipationStatus() != ParticipationStatus.REJECTED) {
                participation.setParticipationStatus(ParticipationStatus.CANCELED);
            }
        }

        moimParticipationRepository.saveAll(participationList);

        // 3. 수락된 신청자들 조회해서 삭제
        List<MoimAccptedMember> acceptMemberList = moimAccptedMemberRepository.findByMoimParticipationIn(participationList);

        moimAccptedMemberRepository.deleteAllInBatch(acceptMemberList);

    }

    @Transactional
    @Override
    public void deletePost(Long moimPostId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        MoimPost moimPost = moimPostRepository.findByMoimPostIdAndMember(moimPostId, member)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));
        moimPostRepository.delete(moimPost);

    }

    // Pageable 유효성 검사 메서드
    public Pageable createPageable(int page, int size) {
        // offset이 -1인 경우, 0으로 변경
        int correctedPage = (page < 0) ? 0 : page;

        // size가 30이나 60이 아닌 경우, 30으로 설정
        int correctedSize = (size == 30 || size == 60) ? size : 30;

        return PageRequest.of(correctedPage, correctedSize);
    }


}
