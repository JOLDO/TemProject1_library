package com.library.project.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // [설명] 브라우저에서 /display/** 로 시작하는 주소로 요청이 오면
        // 실제 내 컴퓨터의 C:/upload/ 폴더에서 파일을 찾으라는 설정이야!
        registry.addResourceHandler("/display/**")
                .addResourceLocations("file:///C:/upload/");
    }
}

/*
 * ========== CustomConfig 설명 ==========
 * - 역할: 업로드된 파일을 브라우저에서 접근 가능하게 하는 정적 리소스 매핑 설정
 *
 * [설정 내용]
 * - /display/** 패턴 요청 → C:/upload/ 폴더에서 파일 탐색
 * - 예: /display/movie/movie1.webp → C:/upload/movie/movie1.webp
 * - FileController와 함께 이미지 파일 서빙에 사용
 */