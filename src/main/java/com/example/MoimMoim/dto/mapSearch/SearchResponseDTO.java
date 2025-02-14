package com.example.MoimMoim.dto.mapSearch;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResponseDTO {
    List<SearchRequestDTO> items;
}
