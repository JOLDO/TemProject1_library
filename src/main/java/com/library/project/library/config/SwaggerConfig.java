package com.library.project.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rest API 백엔드_프로그래밍")   // Swagger UI 상단에 표시될 제목
                        .description("1차 도서 관리 프로젝트")    // API에 대한 간단한 설명
                        .version("1.0.0")                       // API 버전 표기
                );
    }
}