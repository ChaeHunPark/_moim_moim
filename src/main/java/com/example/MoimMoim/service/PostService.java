package com.example.MoimMoim.service;

import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostWriteRequestDTO;

import java.util.List;

public interface PostService {
    // 게시글 작성
    void createPost(PostWriteRequestDTO postWriteRequestDTO);

    // 게시글 단건 조회
    PostResponseDTO viewPost(Long postId);

    // 모든 게시글 조회
    List<PostResponseDTO> getAllPosts();

    // 게시글 수정
    void updatePost(Long postId, PostWriteRequestDTO requestDTO);

    // 게시글 삭제
    void deletePost(Long postId);
}
