package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.post.PostPageResponseDTO;
import com.example.MoimMoim.dto.post.PostResponseDTO;
import com.example.MoimMoim.dto.post.PostRequestDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import com.example.MoimMoim.service.postService.PostService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<?> WritePost(@Valid @RequestBody PostRequestDTO postRequestDTO, BindingResult bindingResult){
        Map<String, String> errors = validationService.validate(bindingResult);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        postService.createPost(postRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","게시글 작성이 완료되었습니다."));
    }


    // 포스트 단건 조회
    @GetMapping("/post-id/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        PostResponseDTO post = postService.viewPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

//    전체 포스트 조회
    @GetMapping("/posts")
    public ResponseEntity<PostPageResponseDTO<PostSummaryResponseDTO>> getAllPosts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "30") int size,
            @RequestParam(name = "category", required = false) String category, // 카테고리별 필터는 옵션임
            @RequestParam(name = "sortBy", defaultValue = "date-asc") String sortBy,
            @RequestParam(name = "keyword", required = false) String keyword, // 키워드도 옵션임
            @RequestParam(name = "searchBy", defaultValue = "title") String searchBy// "title", "content", "title+content"
    ) {

        PostPageResponseDTO<PostSummaryResponseDTO> posts = postService.getPostList(category, sortBy, keyword, searchBy, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 포스트 수정
    @PutMapping("/post-id/{postId}")
    public ResponseEntity<?> updatePost( @PathVariable("postId") Long postId,
                                         @Valid @RequestBody PostRequestDTO postRequestDTO,
                                         BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        postService.updatePost(postId, postRequestDTO);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","게시글 수정이 완료되었습니다."));
    }

    // 포스트 삭제
    @DeleteMapping("/post-id/{postId}")
    public ResponseEntity<?> deletePost( @PathVariable("postId") Long postId,
                                         @RequestParam("memberId") Long memberId) {
        postService.deletePost(postId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","게시글 삭제가 완료되었습니다."));
    }



}
