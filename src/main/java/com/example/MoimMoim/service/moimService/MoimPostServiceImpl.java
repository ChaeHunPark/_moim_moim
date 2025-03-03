package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.domain.*;
import com.example.MoimMoim.dto.moimPost.*;
import com.example.MoimMoim.enums.Category;
import com.example.MoimMoim.enums.EnumUtils;
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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Transactional
public class MoimPostServiceImpl implements MoimPostService {

    private static final String MEMBER_NOT_FOUND = "회원 정보를 찾을 수 없습니다.";
    private static final String POST_NOT_FOUND = "게시글 정보를 찾을 수 없습니다.";
    private static final String POST_NOT_EXIST = "게시글이 존재하지 않습니다.";

    private final MemberRepository memberRepository;
    private final DateTimeUtilService dateTimeUtilService;
    private final MoimPostRepository moimPostRepository;

    // 지역 추출 공통 메서드
    private String extractRegionFromData(String address) {
        return address.split(" ")[0];
    }

    // 회원 찾기
    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_NOT_FOUND));
    }

    // MoimPost 객체로 변환
    private MoimPost convertMoimPost(MoimPostRequestDTO moimPostRequestDTO) {
        Member member = findMember(moimPostRequestDTO.getMemberId());
        return MoimPost.builder()
                .member(member)
                .title(moimPostRequestDTO.getTitle())
                .category(EnumUtils.fromLabel(Category.class, moimPostRequestDTO.getCategory()))
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

    // 게시글 보기: 댓글 정보 포함
    private MoimPostResponseDTO buildMoimPostResponse(MoimPost moimPost) {
        List<MoimCommentResponseDTO> comments = moimPost.getMoimComments() != null ?
                moimPost.getMoimComments().stream()
                        .map(comment -> new MoimCommentResponseDTO(
                                comment.getMoimCommentId(),
                                comment.getMember().getMemberId(),
                                comment.getContent(),
                                comment.getMember().getNickname(),
                                dateTimeUtilService.formatForClient(comment.getCreateAt())
                        ))
                        .collect(Collectors.toList()) : new ArrayList<>();

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
                .category(moimPost.getCategory().getLabel())
                .moimStatus(moimPost.getMoimStatus().getDisplayName())
                .viewCount(moimPost.getViewCount())
                .moimDate(dateTimeUtilService.formatForClient(moimPost.getMoimDate()))
                .createdAt(dateTimeUtilService.formatForClient(moimPost.getCreatedAt()))
                .moimCommentList(comments)
                .build();

        if (moimPost.getUpdateAt() != null) {
            moimPostResponseDTO.setUpdateAt(dateTimeUtilService.formatForClient(moimPost.getUpdateAt()));
        }

        return moimPostResponseDTO;
    }

    // 게시글 작성
    @Override
    public void createMoimPost(MoimPostRequestDTO moimPostRequestDTO) {
        MoimPost moimPost = convertMoimPost(moimPostRequestDTO);
        moimPostRepository.save(moimPost);
    }

    // 게시글 조회
    @Override
    public MoimPostResponseDTO viewMoimPost(Long postId) {
        MoimPost moimPost = moimPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_EXIST));

        moimPost.incrementViewCount();
        moimPostRepository.save(moimPost);

        return buildMoimPostResponse(moimPost);
    }

    // 게시글 목록 조회
    @Override
    public MoimPostPageResponseDTO<MoimPostSummaryResponseDTO> getPostList(
            String category, String sortBy, String keyword, String searchBy,
            String region, String moimStatus, int page, int size) {

        Pageable pageable = createPageable(page - 1, size);
        return moimPostRepository.getPostList(category, sortBy, pageable, keyword, searchBy, region, moimStatus);
    }

    // 게시글 수정
    @Override
    public void updatePost(Long moimPostId, MoimPostRequestDTO requestDTO) {
        Member member = findMember(requestDTO.getMemberId());
        MoimPost moimPost = moimPostRepository.findByMoimPostIdAndMember(moimPostId, member)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));

        moimPost.setTitle(requestDTO.getTitle());
        moimPost.setContent(requestDTO.getContent());
        moimPost.setLocation(requestDTO.getLocation());
        moimPost.setAddress(requestDTO.getAddress());
        moimPost.setRoadAddress(requestDTO.getRoadAddress());
        moimPost.setRegion(extractRegionFromData(requestDTO.getRoadAddress()));
        moimPost.setMapx(requestDTO.getMapx());
        moimPost.setMapy(requestDTO.getMapy());
        moimPost.setCategory(EnumUtils.fromLabel(Category.class, requestDTO.getCategory()));
        moimPost.setMoimDate(requestDTO.getMoimDate());
        moimPost.setUpdateAt(LocalDateTime.now());

        moimPostRepository.save(moimPost);
    }

    // 게시글 삭제
    @Override
    public void deletePost(Long moimPostId, Long memberId) {
        Member member = findMember(memberId);
        MoimPost moimPost = moimPostRepository.findByMoimPostIdAndMember(moimPostId, member)
                .orElseThrow(() -> new PostNotFoundException(POST_NOT_FOUND));
        moimPostRepository.delete(moimPost);
    }

    // Pageable 유효성 검사 메서드
    public Pageable createPageable(int page, int size) {
        int correctedPage = Math.max(0, page);
        int correctedSize = (size == 30 || size == 60) ? size : 30;
        return PageRequest.of(correctedPage, correctedSize);
    }
}
