package com.carelink.global.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

// Spring Security 설정. CORS, CSRF, 인증 방식, 경로별 인가 처리
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CORS — 허용할 origin, method, header 설정
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
                        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Set-Cookie", "X-Requested-With", "Accept"));
                        configuration.setMaxAge(3600L);
                        configuration.setExposedHeaders(List.of("Authorization", "access", "X-Custom-Header"));

                        return configuration;
                    }
                }))
                .csrf((auth) -> auth.disable())       // REST API이므로 CSRF 비활성화
                .formLogin((auth) -> auth.disable())  // 폼 로그인 비활성화
                .httpBasic((auth) -> auth.disable())  // HTTP Basic 인증 비활성화
                // 경로별 인가 작업
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll() // TODO: JWT 필터 추가 후 인증 필요 경로 설정
                );

        return http.build();
    }
}
