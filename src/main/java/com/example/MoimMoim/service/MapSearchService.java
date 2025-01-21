package com.example.MoimMoim.service;

import com.example.MoimMoim.dto.mapSearch.SearchResponseDTO;

public interface MapSearchService {
    SearchResponseDTO addressSearch(String address);
}
