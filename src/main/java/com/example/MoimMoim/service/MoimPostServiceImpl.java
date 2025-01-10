package com.example.MoimMoim.service;


import com.example.MoimMoim.domain.MoimPost;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.repository.MoimPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MoimPostServiceImpl implements MoimPostService{

    private final PostService postService;
    private final MoimPostRepository moimPostRepository;


    @Autowired
    public MoimPostServiceImpl(PostService postService, MoimPostRepository moimPostRepository) {
        this.postService = postService;
        this.moimPostRepository = moimPostRepository;
    }

    @Override
    public void createPost(MoimPostRequestDTO moimPostRequestDTO) {
        Post post = postService.createPost(moimPostRequestDTO.getPostRequestDTO());

        MoimPost moimPost = MoimPost.builder().addressTitle(moimPostRequestDTO.getAddressTitle())
                .addressCategory(moimPostRequestDTO.getAddressCategory())
                .roadAddress(moimPostRequestDTO.getRoadAddress())
                .mapx(moimPostRequestDTO.getMapx())
                .mapy(moimPostRequestDTO.getMapy())
                .post(post)
                .build();

        moimPostRepository.save(moimPost);
    }
}
