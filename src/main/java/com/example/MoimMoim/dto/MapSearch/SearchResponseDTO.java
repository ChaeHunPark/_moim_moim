package com.example.MoimMoim.dto.MapSearch;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchResponseDTO {
    private List<SearchRequestDTO> items;
}
