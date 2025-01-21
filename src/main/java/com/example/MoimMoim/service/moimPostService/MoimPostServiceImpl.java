package com.example.MoimMoim.service.moimPostService;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.dto.moimPost.MoimCommentResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;
import com.example.MoimMoim.enums.MoimStatus;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.MoimPostRepository;
import com.example.MoimMoim.service.utilService.PostUtilService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MoimPostServiceImpl implements MoimPostService{

    private final MemberRepository memberRepository;
    private final PostUtilService postUtilService;
    private final MoimPostRepository moimPostRepository;

    @Autowired
    public MoimPostServiceImpl(MemberRepository memberRepository, PostUtilService postUtilService, MoimPostRepository moimPostRepository) {
        this.memberRepository = memberRepository;
        this.postUtilService = postUtilService;
        this.moimPostRepository = moimPostRepository;
    }

    private String extractRegionFromData(String address){
        return address.split(" ")[0];
    }

    private MoimPost convertMoimPost(MoimPostRequestDTO moimPostRequestDTO){
        return MoimPost.builder()
                .member(postUtilService.findMember(moimPostRequestDTO.getMemberId(), memberRepository))
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
                .moimStatus(MoimStatus.모집중)
                .createdAt(LocalDateTime.now())
                .updateAt(null)
                .viewCount(0L)
                .build();
    }


    @Transactional
    @Override
    public void createMoimPost(MoimPostRequestDTO moimPostRequestDTO) {
        MoimPost moimPost = convertMoimPost(moimPostRequestDTO);
        moimPostRepository.save(moimPost);
    }

    @Override
    public MoimPostResponseDTO viewMoimPost(Long postId) {
        MoimPost moimPost = moimPostRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        moimPost.incrementViewCount();
        moimPostRepository.save(moimPost);


        List<MoimCommentResponseDTO> comments = moimPost.getMoimComments().stream()
                .map(comment -> new MoimCommentResponseDTO(
                        comment.getContent(),
                        comment.getMember().getNickname(),
                        postUtilService.formatDate(comment.getCreateAt())
                )).collect(Collectors.toList());



        MoimPostResponseDTO moimPostResponseDTO = MoimPostResponseDTO.builder()
                .memberId(moimPost.getMember().getMemberId())
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
                .moimDate(String.valueOf(moimPost.getMoimDate()))
                .createdAt(postUtilService.formatDate(moimPost.getCreatedAt()))
                .moimCommentList(comments)
                .build();

        // 업데이트가 null 일수도 있다.
        if(moimPostResponseDTO.getUpdateAt() != null){
            moimPostResponseDTO.setUpdateAt(postUtilService.formatDate(moimPost.getUpdateAt()));
        }



        return moimPostResponseDTO;


    }
}
