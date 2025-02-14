package com.example.MoimMoim.repository;

import com.example.MoimMoim.dto.moim.MoimPostSummaryResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MoimPostRepositoryCustom {
    List<MoimPostSummaryResponseDTO> getPostList(
            String category, String sortBy, Pageable pageable,
            String keyword, String searchBy, String region, String moimStatus);
}
