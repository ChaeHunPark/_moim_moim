package com.example.MoimMoim.service.moimPostService;

import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;

public interface MoimPostService {
    void createMoimPost(MoimPostRequestDTO moimPostRequestDTO);
    MoimPostResponseDTO viewMoimPost(Long postId);
}
