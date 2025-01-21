package com.example.MoimMoim.service.mapService;

import com.example.MoimMoim.dto.mapSearch.SearchResponseDTO;

public interface MapSearchService {
    SearchResponseDTO addressSearch(String address);
}
