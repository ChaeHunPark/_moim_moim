package com.example.MoimMoim.repository;

import com.example.MoimMoim.dto.post.PostPageResponseDTO;
import com.example.MoimMoim.dto.post.PostSummaryResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    PostPageResponseDTO<PostSummaryResponseDTO> findPostsByCategoryAndKeyword(String category, String keyword, String searchBy, String sortBy, Pageable pageable);
}
