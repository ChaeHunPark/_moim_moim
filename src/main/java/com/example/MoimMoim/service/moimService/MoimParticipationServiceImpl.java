package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.domain.*;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationListDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationRequestDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimParticipationResponseDTO;
import com.example.MoimMoim.dto.moimParticipation.MoimPostInParicipationListDTO;
import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.enums.ParticipationStatus;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.moim.*;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimAccptedMemberRepository;
import com.example.MoimMoim.repository.MoimParticipationRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.example.MoimMoim.service.utilService.DateTimeUtilService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MoimParticipationServiceImpl implements MoimParticipationService{

    private final MoimPostRepository moimPostRepository;
    private final MemberRepository memberRepository;
    private final MoimParticipationRepository moimParticipationRepository;
    private final DateTimeUtilService dateTimeUtilService;
    private final JPAQueryFactory jpaQueryFactory;
    private final MoimAccptedMemberRepository moimAccptedMemberRepository;


    @Autowired
    public MoimParticipationServiceImpl(MoimPostRepository moimPostRepository, MemberRepository memberRepository, MoimParticipationRepository moimParticipationRepository, DateTimeUtilService dateTimeUtilService, JPAQueryFactory jpaQueryFactory, MoimAccptedMemberRepository moimAccptedMemberRepository) {
        this.moimPostRepository = moimPostRepository;
        this.memberRepository = memberRepository;
        this.moimParticipationRepository = moimParticipationRepository;
        this.dateTimeUtilService = dateTimeUtilService;
        this.jpaQueryFactory = jpaQueryFactory;
        this.moimAccptedMemberRepository = moimAccptedMemberRepository;
    }

    public MoimParticipation convertMoimParticipation(MoimParticipationRequestDTO moimParticipationRequestDTO,
                                                      Member member,
                                                      MoimPost moimPost) {
        return MoimParticipation.builder()
                .intro(moimParticipationRequestDTO.getIntro())
                .reasonParticipation(moimParticipationRequestDTO.getReasonParticipation())
                .rejection_reason(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .participationStatus(ParticipationStatus.PENDING) // 대기
                .member(member)
                .moimPost(moimPost)
                .build();

    }

    @Override
    public void applyForParticipation(Long moimPostId, Long memberId, MoimParticipationRequestDTO moimParticipationRequestDTO) {
        // 1. 게시글 정보가 필요하다.
        MoimPost moimPost = moimPostRepository.findById(moimPostId)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

        // 2. 신청자 정보가 필요하다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));


        if (moimPost.getMoimStatus() == MoimStatus.FULL) {
            throw new MoimMaxParticipantsReachedException("인원이 마감되었습니다.");
        }
        /*
        * RuntimeException 고칠 것
        * */

        // 본인 게시글에 신청하고 있는가?
        if (moimPost.getMember().getMemberId().equals(member.getMemberId())) {
            throw new InvalidAccessException("잘못된 접근입니다.");
        }

        // 이미 신청한 모임인가?
        boolean isAlreadyApplied = moimParticipationRepository.existsByMoimPostAndMember(moimPost, member);
        if (isAlreadyApplied) {
            throw new AlreadyAppliedException("이미 신청한 모임입니다.");
        }


        MoimParticipation moimParticipation = convertMoimParticipation(moimParticipationRequestDTO, member, moimPost);
        moimParticipationRepository.save(moimParticipation);
    }


    public MoimParticipationResponseDTO createMoimParticipationResponseDTO(MoimParticipation moimParticipation, MoimPost moimPost) {

        MoimParticipationResponseDTO moimParticipationResponseDTO = MoimParticipationResponseDTO.builder()
                .moimParticipationRequestId(moimParticipation.getMoimParticipationRequestId()).moimPostId(moimPost.getMoimPostId())
                .region(moimPost.getRegion())
                .Category(moimPost.getCategory().getLabel())
                .hostNickname(moimPost.getMember().getNickname())
                .nickname(moimParticipation.getMember().getNickname())
                .intro(moimParticipation.getIntro())
                .reasonParticipation(moimParticipation.getReasonParticipation())
                .moimDate(dateTimeUtilService.formatForClient(moimPost.getMoimDate()))
                .moimStatus(moimPost.getMoimStatus().getDisplayName())
                .ParticipationStatus(moimParticipation.getParticipationStatus().getLabel())
                .createdAt(dateTimeUtilService.formatForClient(moimParticipation.getCreatedAt()))
                .build();

        if(moimParticipation.getUpdatedAt() != null){
            moimParticipationResponseDTO.setUpdatedAt(dateTimeUtilService.formatForClient(LocalDateTime.now()));
        }

        return moimParticipationResponseDTO;
    }


    @Override
    public MoimParticipationResponseDTO getMyParticipation(Long memberId, Long participationId) {


        // 1. 신청 정보가 존재하는가?
        MoimParticipation moimParticipation = moimParticipationRepository.findById(participationId)
                .orElseThrow(() -> new MoimParticipationNotFoundException("신청 정보가 존재하지 않습니다."));

        MoimPost moimPost = moimParticipation.getMoimPost();

        // 2. 게시글 정보가 존재하는가?
        if (moimPost == null) {
            throw new PostNotFoundException("게시글이 존재하지 않습니다.");
        }

        // 2-1 신청 정보의 id와 member의 id가 다른가?
        if (!moimParticipation.getMember().getMemberId().equals(memberId)){
            throw new MemberInfoMismatchException("회원 정보가 일치하지 않습니다.");
        }


        return createMoimParticipationResponseDTO(moimParticipation, moimPost);
    }

    @Override
    public MoimParticipationResponseDTO getReceivedParticipation(Long ownerId, Long participationId) {
        // 1. 신청 정보가 존재하는가?
        MoimParticipation moimParticipation = moimParticipationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationNotFoundException("신청 정보가 존재하지 않습니다."));

        MoimPost moimPost = moimParticipation.getMoimPost();

        // 2. 게시글 정보가 존재하는가?
        if (moimPost == null) {
            throw new PostNotFoundException("게시글이 존재하지 않습니다.");
        }

        // 2-1 모임 포스트 작성자의 id와 ownerId의 id가 다른가?
        if (!moimPost.getMember().getMemberId().equals(ownerId)){
            throw new MemberInfoMismatchException("회원 정보가 일치하지 않습니다.");
        }


        return createMoimParticipationResponseDTO(moimParticipation, moimPost);
    }

    @Override
    public List<MoimPostInParicipationListDTO> getApplicantParticipationList(Long applicantId) {
        return getParticipationList(applicantId, "applicantId");
    }

    @Override
    public List<MoimPostInParicipationListDTO> getReceivedParticipationList(Long receiverId) {
        return getParticipationList(receiverId, "receiverId");
    }

    private List<MoimPostInParicipationListDTO> getParticipationList(Long memberId, String userType) {
        QMember member = QMember.member;
        QMoimPost moimPost = QMoimPost.moimPost;
        QMoimParticipation participation = QMoimParticipation.moimParticipation;

        List<Tuple> result = List.of();

// 한 번의 쿼리로 모든 데이터를 가져오기
        JPAQuery<Tuple> select = jpaQueryFactory
                .select(
                        moimPost.moimPostId,
                        moimPost.title,
                        moimPost.region,
                        moimPost.category.stringValue(),
                        moimPost.member.nickname,
                        moimPost.currentParticipants,
                        moimPost.maxParticipants,
                        moimPost.moimStatus.stringValue(),
                        moimPost.moimDate.stringValue(),
                        moimPost.createdAt.stringValue(),
                        participation.moimParticipationRequestId,
                        participation.member.nickname,
                        participation.participationStatus.stringValue(),
                        participation.createdAt.stringValue()
                );

        if(userType.equals("receiverId"))
            result = select
                    .from(moimPost)
                    .leftJoin(participation).on(participation.moimPost.moimPostId.eq(moimPost.moimPostId))  // 모임에 대한 신청자 LEFT JOIN
                    .where(moimPost.member.memberId.eq(memberId))
                    .fetch();
        if(userType.equals("applicantId"))
            result = select
                    .from(participation) // 신청자 관점이므로 participation을 기준으로 가져옴
                    .join(participation.moimPost, moimPost) // 모임 정보를 가져오기 위해 조인
                    .where(participation.member.memberId.eq(memberId)) // 특정 신청자가 신청한 모임만 조회
                    .fetch();




//  `moimPostId` 기준으로 그룹핑
        Map<Long, MoimPostInParicipationListDTO> moimPostMap = new HashMap<>();

        for (Tuple row : result) {
            Long moimPostId = row.get(moimPost.moimPostId);

            //  해당 `moimPostId` 가 처음 등장한 경우 DTO 생성
            moimPostMap.putIfAbsent(moimPostId, new MoimPostInParicipationListDTO(
                    moimPostId,
                    row.get(moimPost.title),
                    row.get(moimPost.region),
                    row.get(moimPost.category.stringValue()),
                    row.get(moimPost.member.nickname),
                    row.get(moimPost.currentParticipants),
                    row.get(moimPost.maxParticipants),
                    row.get(moimPost.moimStatus.stringValue()),
                    row.get(moimPost.moimDate.stringValue()),
                    row.get(moimPost.createdAt.stringValue()),
                    new ArrayList<>() // 참여자 리스트 초기화
            ));

            //  참여 신청 데이터가 있는 경우 리스트에 추가
            if (row.get(participation.moimParticipationRequestId) != null) {
                moimPostMap.get(moimPostId).getParticipationList().add(new MoimParticipationListDTO(
                        row.get(participation.moimParticipationRequestId),
                        moimPostId,
                        row.get(participation.member.nickname),
                        row.get(participation.participationStatus.stringValue()),
                        row.get(participation.createdAt.stringValue())
                ));
            }
        }

// 5️⃣ 결과 반환
        return new ArrayList<>(moimPostMap.values());
    }

    // 상태가 이미 수락(ACCEPTED) 또는 거절(REJECTED)인지 확인하는 메서드
    private void checkIfParticipationStatusIsFinal(MoimParticipation participation) {
        if (participation.getParticipationStatus() == ParticipationStatus.ACCEPTED ||
                participation.getParticipationStatus() == ParticipationStatus.REJECTED) {
            throw new ParticipationStatusFinalException("이미 상태가 변경된 신청입니다. 더 이상 수락 또는 거절할 수 없습니다.");
        }
    }

    @Transactional
    @Override
    public void acceptParticipation(Long participationId, Long ownerId) {
        MoimParticipation participation = moimParticipationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationNotFoundException("참여 신청을 찾을 수 없습니다."));


        MoimPost moimPost = moimPostRepository.findById(participation.getMoimPost().getMoimPostId())
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        // 해당 모임이 신청자의 모임 게시글이 아니면 에러 처리
        if (!participation.getMoimPost().getMember().getMemberId().equals(ownerId)) {
            throw new MoimOwnerMismatchException("이 모임의 주최자가 아닙니다.");
        }

        // 해당 모임의 인원이 만원이면.
        if(moimPost.getCurrentParticipants() >= moimPost.getMaxParticipants()) {
            throw new MoimMaxParticipantsReachedException("모임의 최대 인원에 도달했습니다.");
        }

        // 상태가 이미 수락이나 거절인지
        checkIfParticipationStatusIsFinal(participation);


        // 상태를 '수락'으로 변경
        participation.setParticipationStatus(ParticipationStatus.ACCEPTED);
        participation.setUpdatedAt(LocalDateTime.now());
        moimParticipationRepository.save(participation);  // 상태 업데이트

        // 참여자를 늘린다.
        moimPost.incrementCurrentParticipants();

        MoimAccptedMember moimAccptedMember = MoimAccptedMember.builder()
                .acceptedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .moimParticipation(participation)
                .member(participation.getMember())
                .build();

        // 수락 목록에 저장
        moimAccptedMemberRepository.save(moimAccptedMember);


    }

    @Override
    public void rejectParticipation(Long participationId, Long ownerId, String rejectionReason) {
        MoimParticipation participation = moimParticipationRepository.findById(participationId)
                .orElseThrow(() -> new ParticipationNotFoundException("참여 신청을 찾을 수 없습니다."));

        Long getOwnerId = participation.getMoimPost().getMember().getMemberId();

        // 해당 모임이 신청자의 모임 게시글이 아니면 에러 처리
        if (!getOwnerId.equals(ownerId)) {
            throw new MoimOwnerMismatchException("이 모임의 주최자가 아닙니다.");
        }

        // 상태가 이미 수락이나 거절인지
        checkIfParticipationStatusIsFinal(participation);

        // 상태를 '거절'로 변경하고 거절 이유를 설정
        participation.setParticipationStatus(ParticipationStatus.REJECTED);
        participation.setRejection_reason(rejectionReason);  // 거절 이유 저장
        participation.setUpdatedAt(LocalDateTime.now());
        moimParticipationRepository.save(participation);  // 상태 및 거절 이유 업데이트
    }

    // 상태에 따라 참여자 조회 메서드
    private List<MoimParticipationListDTO> getParticipantsByStatus(Long moimPostId, ParticipationStatus status) {
        QMoimAccptedMember acceptedMember = QMoimAccptedMember.moimAccptedMember;
        QMoimParticipation participation = QMoimParticipation.moimParticipation;
        QMoimPost moimPost = QMoimPost.moimPost;
        QMember member = QMember.member;

        return jpaQueryFactory
                .select(Projections.constructor(MoimParticipationListDTO.class,
                        acceptedMember.moimParticipation.moimParticipationRequestId, // 신청 고유 ID
                        moimPost.moimPostId, // 모임 고유 ID
                        moimPost.region, // 지역
                        moimPost.category.stringValue(), // 카테고리 이름
                        moimPost.moimDate.stringValue(), // 모임 날짜
                        member.nickname, // 신청자 닉네임
                        participation.participationStatus.stringValue(), // 참여 상태
                        participation.createdAt.stringValue())) // 신청 생성 시간
                .from(acceptedMember)
                .join(acceptedMember.moimParticipation, participation)  // MoimParticipation과 조인
                .join(participation.moimPost, moimPost)  // MoimPost와 조인
                .join(participation.member, member)  // 참여자(회원)과 조인
                .where(moimPost.moimPostId.eq(moimPostId) // 모임 ID로 필터링
                        .and(participation.participationStatus.eq(status)))  // 상태에 맞는 참여자만 필터링
                .fetch();
    }

    @Override
    public List<MoimParticipationListDTO> getAcceptedParticipants(Long moimPostId) {
        return getParticipantsByStatus(moimPostId, ParticipationStatus.ACCEPTED);
    }

    @Override
    public List<MoimParticipationListDTO> getRejectedParticipationList(Long moimPostId) {
        return getParticipantsByStatus(moimPostId, ParticipationStatus.REJECTED);
    }



    // 모임 취소
    @Override
    public void cancellationMoimPost(Long moimPostId, String reason) {

        MoimPost moimPost = moimPostRepository.findById(moimPostId)
                .orElseThrow(() -> new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

        // 1. 포스트 취소 상태로 변경
        moimPost.setMoimStatus(MoimStatus.CANCELED);
        moimPost.setUpdateAt(LocalDateTime.now());
        moimPost.setCancellationReason(reason);

        moimPostRepository.save(moimPost);

        // 2. 모임 신청자들의 상태 변경
        List<MoimParticipation> participationList = moimParticipationRepository.findByMoimPost(moimPost);

        // 거절한 사람은 상태 변경이 필요없음.
        for (MoimParticipation participation : participationList) {
            if(participation.getParticipationStatus() != ParticipationStatus.REJECTED) {
                participation.setParticipationStatus(ParticipationStatus.CANCELED);
                participation.setUpdatedAt(LocalDateTime.now());
            }
        }

        moimParticipationRepository.saveAll(participationList);

        // 3. 수락된 신청자들 조회해서 삭제
        List<MoimAccptedMember> acceptMemberList = moimAccptedMemberRepository.findByMoimParticipationIn(participationList);

        moimAccptedMemberRepository.deleteAllInBatch(acceptMemberList);

    }
}
