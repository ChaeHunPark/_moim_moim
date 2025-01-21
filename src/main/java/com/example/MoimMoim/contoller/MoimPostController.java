package com.example.MoimMoim.contoller;

import com.example.MoimMoim.common.ValidationService;
import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.dto.moimPost.MoimPostResponseDTO;
import com.example.MoimMoim.service.moimPostService.MoimPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/write")
    public ResponseEntity<?> WritePost(@Valid @RequestBody MoimPostRequestDTO moimPostRequestDTO, BindingResult bindingResult){
        Map<String, String> errors = validationService.validate(bindingResult);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        moimPostService.createMoimPost(moimPostRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("모임 게시글 작성이 완료되었습니다.");
    }

    @GetMapping("/post-id/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") Long postId) {
        MoimPostResponseDTO moimPostResponseDTO = moimPostService.viewMoimPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(moimPostResponseDTO);
    }

}
