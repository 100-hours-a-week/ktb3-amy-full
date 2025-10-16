package com.example.community.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {
    @Bean
    public OpenAPI communityAPI() {
        return new OpenAPI().info(new Info().title("커뮤니티 API").description("회원 가입, 게시글, 댓글, 좋아요 관련 API 입니다.").version("v1.0"));
    }
}
