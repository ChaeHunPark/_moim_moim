package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.MapSearch.SearchResponseDTO;
import com.example.MoimMoim.service.MapSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class MapSearchController {

    private final MapSearchService mapSearchService;

    public MapSearchController(MapSearchService mapSearchService) {
        this.mapSearchService = mapSearchService;
    }

    // 주소 검색
    @GetMapping("/{address}")
    public ResponseEntity<SearchResponseDTO> ResSearch(@PathVariable("address") String address){
        SearchResponseDTO searchResponseDTO = mapSearchService.addressSearch(address);
        return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
    }

}
