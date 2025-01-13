package com.example.MoimMoim.service;

import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.dto.comment.CommentResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;

import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.example.MoimMoim.util.PostUtilService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoimPostServiceImpl implements MoimPostService{

    private final MoimPostRepository moimPostRepository;
    private final MemberRepository memberRepository;
    private final PostUtilService postUtilService;

    @Autowired
    public MoimPostServiceImpl(MoimPostRepository moimPostRepository, MemberRepository memberRepository, PostUtilService postUtilService) {
        this.moimPostRepository = moimPostRepository;
        this.memberRepository = memberRepository;
        this.postUtilService = postUtilService;
    }

    // 행정구역 필터링 메소드
    private String extractRegionFromAddress(String address) {
        // 예시: "경기도 하남시 신장동 411-18"에서 "경기도" 추출
        String[] addressParts = address.split(" ");  // 공백 기준으로 분리
        if (addressParts.length > 0) {
            return addressParts[0];  // 첫 번째 부분을 지역으로 간주
        }
        return "";
    }


    @Override
    public void createPost(MoimPostRequestDTO moimPostRequestDTO) {
        MoimPost moimPost = MoimPost.builder()
                .title(moimPostRequestDTO.getTitle())
                .category(moimPostRequestDTO.getCategory())
                .content(moimPostRequestDTO.getContent())
                .location(moimPostRequestDTO.getLocation())
                .address(moimPostRequestDTO.getAddress())
                .roadAddress(moimPostRequestDTO.getRoadAddress())
                .region(extractRegionFromAddress(moimPostRequestDTO.getAddress())) // 행정구역 저장
                .mapy(moimPostRequestDTO.getMapy())
                .mapx(moimPostRequestDTO.getMapx())
                .moimStatus(MoimStatus.모집중)
                .maxParticipants(moimPostRequestDTO.getMaxParticipants())
                .currentParticipants(1)
                .moimDate(moimPostRequestDTO.getMoimDate())
                .createAt(LocalDateTime.now())
                .viewCount(0L)
                .member(postUtilService.findMember(moimPostRequestDTO.getMemberId(),memberRepository))
                .build();
        moimPostRepository.save(moimPost);
    }

    @Override
    public MoimPostResponseDTO viewPost(Long postId) {
        MoimPost moimpost = moimPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // 게시글 조회 후 카운트 증가 및 데이터베이스에 저장
        moimpost.incrementViewCount();
        moimPostRepository.save(moimpost);

        // 댓글리스트 조회
        List<CommentResponseDTO> comments = moimpost.getComments().stream()
                .map(comment ->
                        new CommentResponseDTO(
                                comment.getContent(),
                                comment.getMember().getNickname(),
                                postUtilService.formatDate(comment.getCreate_at())))
                .collect(Collectors.toList());



        return MoimPostResponseDTO.builder()
                .memberId(moimpost.getMember().getMemberId())
                .title(moimpost.getTitle())
                .category(moimpost.getCategory())
                .content(moimpost.getContent())
                .location(moimpost.getLocation())
                .address(moimpost.getAddress())
                .roadAddress(moimpost.getRoadAddress())
                .region(moimpost.getRegion())
                .mapx(moimpost.getMapx())
                .mapy(moimpost.getMapy())
                .currentParticipants(moimpost.getCurrentParticipants())
                .maxParticipants(moimpost.getMaxParticipants())
                .moimStatus(moimpost.getMoimStatus())
                .moimDate(postUtilService.formatDate(moimpost.getMoimDate()))
                .createAt(postUtilService.formatDate(LocalDateTime.now()))
                .commentList(comments)
                .viewCount(moimpost.getViewCount())
                .build();

    }
}
