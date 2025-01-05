package com.example.MoimMoim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 필터체인 커스텀하기.
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // RestController를 위한 csrf 해제
        http.csrf(AbstractHttpConfigurer::disable);


        // 모든 경로를 모두 허용해준다.
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll()
                .anyRequest().authenticated());

        // 세션을 사용하지 않기 때문에 STATELESS로 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // form login disable
        http.formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
