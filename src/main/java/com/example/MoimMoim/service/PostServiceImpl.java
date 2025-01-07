package com.example.MoimMoim.service;

import com.example.MoimMoim.domain.Member;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostWriteRequestDTO;
import com.example.MoimMoim.exception.member.MemberNotFoundException;
import com.example.MoimMoim.exception.post.PostNotFoundException;
import com.example.MoimMoim.repository.MemberRepository;
import com.example.MoimMoim.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public Member findMember(Long id){
        return memberRepository.findById(id).
                orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));
    }



    public Post convertPost(PostWriteRequestDTO postWriteRequestDTO){
        return Post.builder()
                    .title(postWriteRequestDTO.getTitle())
                    .category(postWriteRequestDTO.getCategory())
                    .content(postWriteRequestDTO.getContent())
                    .createAt(LocalDateTime.now())
                    .viewCount(0L)
                    .member(findMember(postWriteRequestDTO.getMemberId()))
                    .build();
    }


    //시간 포맷팅
    private String formatDate(LocalDateTime createAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        return createAt.format(formatter);
    }


    @Transactional
    @Override
    public void createPost(PostWriteRequestDTO postWriteRequestDTO) {
        Post post = convertPost(postWriteRequestDTO);
        postRepository.save(post);
    }


    // 게시글 단건 조회
    @Override
    public PostResponseDTO viewPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글이 존재하지 않습니다."));

        // 게시글 조회 후 카운트 증가 및 데이터베이스에 저장
        post.incrementViewCount();
        postRepository.save(post);


        return PostResponseDTO.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .content(post.getContent())
                .nickname(post.getMember().getNickname())
                .createAt(formatDate(post.getCreateAt()))
                .viewCount(post.getViewCount())
                .build();
    }

    // 전체 게시글 조회
    @Override
    public List<PostResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> {
                    // Post 객체를 PostResponseDTO로 변환
                    PostResponseDTO postResponseDTO = new PostResponseDTO();
                    postResponseDTO.setPostId(post.getPostId());
                    postResponseDTO.setTitle(post.getTitle());
                    postResponseDTO.setCategory(post.getCategory()); // Enum 타입 그대로 사용
                    postResponseDTO.setContent(post.getContent());
                    postResponseDTO.setCreateAt(formatDate(post.getCreateAt())); // formatDate 메서드를 사용해 날짜 포맷팅
                    postResponseDTO.setNickname(post.getMember().getNickname()); // Member 객체에서 nickname 가져오기
                    postResponseDTO.setViewCount(post.getViewCount());
                    return postResponseDTO;
                })
                .collect(Collectors.toList()); // 리스트 반환
    }

    @Override
    public void updatePost(Long postId, PostWriteRequestDTO postWriteRequestDTO) {
        // 1. 포스트 아이디로 게시글 찾기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));


        // 2. 게시글 수정
        post.setTitle(postWriteRequestDTO.getTitle());
        post.setCategory(postWriteRequestDTO.getCategory());
        post.setContent(post.getContent());

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        postRepository.delete(post);
    }

}
