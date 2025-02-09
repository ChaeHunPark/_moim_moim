package com.example.MoimMoim.service.postService;

import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    // 게시글 작성
    void createPost(PostRequestDTO postRequestDTO);

    // 게시글 단건 조회
    PostResponseDTO viewPost(Long postId);

    // 모든 게시글 조회
    List<PostSummaryResponseDTO> getPostList(String category,
                                             String keyword,
                                             String searchBy,
                                             String sortBy,
                                             int page,
                                             int size);

    // 게시글 수정
    void updatePost(Long postId, PostRequestDTO requestDTO);

    // 게시글 삭제
    void deletePost(Long postId, Long memberId);
}
