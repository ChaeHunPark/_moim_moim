package com.example.MoimMoim.contoller;

import com.example.MoimMoim.dto.mapSearch.SearchResponseDTO;
import com.example.MoimMoim.service.mapService.MapSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class MapSearchController {

    private final MapSearchService mapSearchService;

    public MapSearchController(MapSearchService mapSearchService) {
        this.mapSearchService = mapSearchService;
    }

    // 주소 검색
    @GetMapping("/")
    public ResponseEntity<SearchResponseDTO> ResSearch(@RequestParam("address") String address){
        SearchResponseDTO searchResponseDTO = mapSearchService.addressSearch(address);
        return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
    }

}
