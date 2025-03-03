package com.example.MoimMoim.service.moimService;

import com.example.MoimMoim.dto.moimPost.MoimPostPageResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostSummaryResponseDTO;


import java.util.List;

public interface MoimPostService {


    void createMoimPost(MoimPostRequestDTO moimPostRequestDTO);

    MoimPostResponseDTO viewMoimPost(Long postId);


    MoimPostPageResponseDTO<MoimPostSummaryResponseDTO> getPostList(
            String category, String sortBy, String keyword, String searchBy,
            String region, String moimStatus, int page, int size);

    // 게시글 수정
    void updatePost(Long moimPostId, MoimPostRequestDTO requestDTO);

    // 게시글 삭제
    void deletePost(Long moimPostId, Long memberId);

}
