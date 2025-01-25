package com.example.MoimMoim.service.moimPostService;

import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostSummaryResponseDTO;
import com.example.MoimMoim.enums.MoimStatus;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface MoimPostService {


    void createMoimPost(MoimPostRequestDTO moimPostRequestDTO);

    MoimPostResponseDTO viewMoimPost(Long postId);

    List<MoimPostSummaryResponseDTO> getPostList(String category,
                                                 String sortBy,
                                                 Pageable pageable,
                                                 String keyword,
                                                 String searchBy,
                                                 String region,
                                                 String moimStatus);

    // 게시글 수정
    void updatePost(Long moimPostId, MoimPostRequestDTO requestDTO);

    // 게시글 삭제
    void deletePost(Long moimPostId, Long memberId);
}
