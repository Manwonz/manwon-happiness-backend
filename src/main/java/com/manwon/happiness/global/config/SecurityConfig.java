package com.manwon.happiness.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/health", "/users")
//                        .permitAll() // 특정 GET 엔드포인트 허용
//                        .anyRequest().authenticated() // 나머지는 인증 필요
//                ) // 인증만 요구
//                .httpBasic(Customizer.withDefaults()); // Basic Auth 사용
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // 전부 열기

        return http.build();
    }
}