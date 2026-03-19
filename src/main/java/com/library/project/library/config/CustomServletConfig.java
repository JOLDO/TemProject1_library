package com.library.project.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CustomServletConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // /js/** 패턴 요청 → src/main/resources/static/js/ 폴더에서 파일 제공
        // 예) /js/booklist.js → static/js/booklist.js
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // /css/** 패턴 요청 → src/main/resources/static/css/ 폴더에서 파일 제공
        // 예) /css/style.css → static/css/style.css
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // /** 패턴: 위에서 매칭되지 않은 나머지 모든 정적 자원 요청
        // → static/ 최상위 폴더에서 파일을 찾음
        // 예) /favicon.ico → static/favicon.ico
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}