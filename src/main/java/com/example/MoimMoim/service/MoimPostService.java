
package com.example.MoimMoim.service;

import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;
import com.example.MoimMoim.dto.post.PostResponseDTO;


public interface MoimPostService {
    void createPost(MoimPostRequestDTO moimPostRequestDTO);

    // 게시글 단건 조회
    MoimPostResponseDTO viewPost(Long postId);
}
