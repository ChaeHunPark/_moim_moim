package com.example.MoimMoim.service;

import com.example.MoimMoim.dto.MapSearch.SearchRequestDTO;
import com.example.MoimMoim.dto.MapSearch.SearchResponseDTO;

public interface MapSearchService {
    SearchResponseDTO addressSearch(String address);
}
