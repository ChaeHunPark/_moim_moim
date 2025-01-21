package com.example.MoimMoim.service;

import com.example.MoimMoim.config.MapProperties;


import com.example.MoimMoim.dto.mapSearch.SearchResponseDTO;
import com.example.MoimMoim.exception.mapSearch.ClientErrorException;
import com.example.MoimMoim.exception.mapSearch.ServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;


@Service
public class MapSearchServiceImpl implements MapSearchService {

    private final MapProperties mapProperties;

    @Autowired
    public MapSearchServiceImpl(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
    }

    @Override
    public SearchResponseDTO addressSearch(String address) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/local.json") // 제이슨 형식으로 반환
                .queryParam("query", address) // 검색 파라미터
                .queryParam("display", 5) // 5개 까지 표시
                .queryParam("start", 1) // 검색 시작 위치
                .queryParam("sort", "random") // random : 정확도순 내림차순 정렬
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();


        // 헤더에 데이터 추가
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", mapProperties.getClientId())
                .defaultHeader("X-Naver-Client-Secret", mapProperties.getClientSecret())
                .build();

        SearchResponseDTO searchRequestDTO = webClient.get()
                .uri(uri)//uri 요청 경로
                .retrieve()//응답을 가져오는 메서드
                .onStatus(HttpStatusCode::is4xxClientError,
                        res -> Mono.error(new ClientErrorException("클라이언트 오류가 발생하였습니다.")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        res -> Mono.error(new ServerErrorException("서버 오류가 발생하였습니다.")))
                .bodyToMono(SearchResponseDTO.class)
                .block();


        return searchRequestDTO;
    }
}
