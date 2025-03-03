package com.example.MoimMoim.dto.moimPost;

import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class MoimPostPageResponseDTO<T> {
    private List<MoimPostSummaryResponseDTO> content;   // 게시글 목록 (PostSummaryResponseDTO 리스트)
    private int totalPages;    // 전체 페이지 개수
    private long totalElements; // 전체 게시글 개수
    private int currentPage;   // 현재 페이지 번호
    private int pageSize;      // 페이지 크기
}
