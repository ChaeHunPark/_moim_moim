package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.moimPost.MoimCommentRequestDTO;
import com.example.MoimMoim.service.moimService.MoimCommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/moim-comment")
public class MoimPostCommentController {

    private final MoimCommentService moimCommentService;
    private final ValidationService validationService;

    @Autowired
    public MoimPostCommentController(MoimCommentService moimCommentService, ValidationService validationService) {
        this.moimCommentService = moimCommentService;
        this.validationService = validationService;
    }

    // 댓글 작성
    @PostMapping("/write")
    public ResponseEntity<?> createComment(@RequestBody MoimCommentRequestDTO request, BindingResult bindingResult) {
        // 유효성 검증 실패 처리
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        moimCommentService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message","댓글 작성이 완료되었습니다."));
    }

    // 댓글 수정
    @PutMapping("/moim-comment-id/{moimCommentId}")
    public ResponseEntity<?> updateComment(@RequestBody MoimCommentRequestDTO request,
                                                @PathVariable("moimCommentId") Long moimCommentId) {
        moimCommentService.updateComment(request, moimCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","댓글이 성공적으로 수정되었습니다."));
    }

    // 댓글 삭제
    @DeleteMapping("/moim-comment-id/{moimCommentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("moimCommentId") Long moimCommentId,
                                           @Valid @RequestBody MoimCommentRequestDTO moimCommentRequestDTO) {
        moimCommentService.deleteComment(moimCommentRequestDTO, moimCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","댓글 삭제가 완료되었습니다."));
    }
}
