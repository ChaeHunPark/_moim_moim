package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.post.CommentRequestDTO;
import com.example.MoimMoim.service.postService.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final ValidationService validationService;
    private final CommentService commentService;

    @Autowired
    public CommentController(ValidationService validationService, CommentService commentService) {
        this.validationService = validationService;
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping("/write")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequestDTO commentRequestDTO, BindingResult bindingResult ) {
        // 유효성 검증 실패 처리
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        commentService.createComment(commentRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 작성이 완료되었습니다.");

    }

    // 댓글 수정
    @PutMapping("/comment-id/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
            commentService.updateComment(commentRequestDTO, commentId);
            return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 수정되었습니다.");

    }

    // 댓글 삭제
    @DeleteMapping("/comment-id/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId,
                                           @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        commentService.deleteComment(commentRequestDTO, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("댓글 삭제가 완료되었습니다.");
    }
}
