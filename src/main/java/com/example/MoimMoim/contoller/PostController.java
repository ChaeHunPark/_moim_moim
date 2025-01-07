package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.domain.Post;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostWriteRequestDTO;
import com.example.MoimMoim.service.PostService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final ValidationService validationService;

    @Autowired
    public PostController(PostService postService, ValidationService validationService) {
        this.postService = postService;
        this.validationService = validationService;
    }


    @PostMapping("/write")
    public ResponseEntity<?> WritePost(@Valid @RequestBody PostWriteRequestDTO postWriteRequestDTO, BindingResult bindingResult){
        Map<String, String> errors = validationService.validate(bindingResult);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        postService.createPost(postWriteRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 작성이 완료되었습니다.");
    }


    // 포스트 단건 조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        PostResponseDTO post = postService.viewPost(postId);
        return ResponseEntity.ok(post);
    }

    // 전체 포스트 조회
    @GetMapping("/all")
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        List<PostResponseDTO> posts = postService.getAllPosts();

        return ResponseEntity.ok(posts);
    }

    // 포스트 수정
    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost( @PathVariable("postId") Long postId,
                                         @Valid @RequestBody PostWriteRequestDTO postWriteRequestDTO,
                                         BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        postService.updatePost(postId, postWriteRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 수정이 완료되었습니다.");
    }

    // 포스트 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost( @PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.CREATED).body("게시글 삭제가 완료되었습니다.");
    }




}
