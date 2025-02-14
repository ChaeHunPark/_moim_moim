package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.moim.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moim.MoimPostResponseDTO;
import com.example.MoimMoim.dto.moim.MoimPostSummaryResponseDTO;
import com.example.MoimMoim.service.moimService.MoimPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moim-post")
public class MoimPostController {

    private final MoimPostService moimPostService;
    private final ValidationService validationService;

    @Autowired
    public MoimPostController(MoimPostService moimPostService, ValidationService validationService) {
        this.moimPostService = moimPostService;
        this.validationService = validationService;
    }

    // 게시글 생성
    @PostMapping("/write")
    public ResponseEntity<?> WritePost(@Valid @RequestBody MoimPostRequestDTO moimPostRequestDTO, BindingResult bindingResult){
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        moimPostService.createMoimPost(moimPostRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임 게시글 작성이 완료되었습니다.");
    }

    // 게시글 상세 조회
    @GetMapping("/moim-post-id/{moimPostId}")
    public ResponseEntity<?> getPost(@PathVariable("moimPostId") Long moimPostId) {
        MoimPostResponseDTO moimPostResponseDTO = moimPostService.viewMoimPost(moimPostId);
        return ResponseEntity.status(HttpStatus.OK).body(moimPostResponseDTO);
    }

    // 게시글 목록 조회
    @GetMapping("/moim-posts")
    public ResponseEntity<List<MoimPostSummaryResponseDTO>> getPostList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "30") int size,
            @RequestParam(name = "category", required = false) String category, // 카테고리별 필터는 옵션임
            @RequestParam(name = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(name = "keyword", required = false) String keyword, // 키워드도 옵션임
            @RequestParam(name = "searchBy", defaultValue = "title") String searchBy,
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "moimStatus", required = false) String moimStatus
            ) {

        List<MoimPostSummaryResponseDTO> posts = moimPostService.getPostList(
                category, sortBy, keyword, searchBy,
                region, moimStatus, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    // 게시글 수정
    @PutMapping("/moim-post-id/{moimPostId}")
    public ResponseEntity<?> updatePost(@PathVariable("moimPostId") Long moimPostId,
                                        @RequestBody MoimPostRequestDTO requestDTO,
                                        BindingResult bindingResult) {
        Map<String, String> errors = validationService.validate(bindingResult);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        moimPostService.updatePost(moimPostId, requestDTO);

        return ResponseEntity.status(HttpStatus.OK).body("게시글 수정이 완료되었습니다.");
    }

    // 게시글 삭제
    @DeleteMapping("/moim-post-id/{moimPostId}")
    public ResponseEntity<String> deletePost(
            @PathVariable("moimPostId") Long moimPostId,
            @RequestParam("memberId") Long memberId) {
        moimPostService.deletePost(moimPostId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body("게시글 삭제가 완료되었습니다.");
    }

}
