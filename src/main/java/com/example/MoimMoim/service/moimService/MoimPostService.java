package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.dto.moim.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moim.MoimPostResponseDTO;
import com.example.MoimMoim.dto.moim.MoimPostSummaryResponseDTO;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface MoimPostService {


    void createMoimPost(MoimPostRequestDTO moimPostRequestDTO);

    MoimPostResponseDTO viewMoimPost(Long postId);


    List<MoimPostSummaryResponseDTO> getPostList(
            String category, String sortBy, String keyword, String searchBy,
            String region, String moimStatus, int page, int size);

    // 게시글 수정
    void updatePost(Long moimPostId, MoimPostRequestDTO requestDTO);

    // 게시글 삭제
    void deletePost(Long moimPostId, Long memberId);

    //모임 취소
    void cancellationMoimPost(Long moimPostId, String reason);
}
