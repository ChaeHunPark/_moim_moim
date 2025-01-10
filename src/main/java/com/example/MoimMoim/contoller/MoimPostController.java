package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.moimPost.MoimPostRequestDTO;
import com.example.MoimMoim.service.MoimPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moim-post")
public class MoimPostController {
    private final MoimPostService moimPostService;

    public MoimPostController(MoimPostService moimPostService) {
        this.moimPostService = moimPostService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody MoimPostRequestDTO moimPostRequestDTO){
        moimPostService.createPost(moimPostRequestDTO);
        return ResponseEntity.ok("ok");
    }
}
