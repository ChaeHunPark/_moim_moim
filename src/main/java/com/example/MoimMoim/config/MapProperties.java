package com.example.MoimMoim.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MapProperties {

    @Value("${map.clientId}")
    private String clientId;

    @Value("${map.clientSecret}")
    private String clientSecret;

}
