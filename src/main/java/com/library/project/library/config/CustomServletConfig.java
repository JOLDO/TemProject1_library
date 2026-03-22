package com.library.project.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
        // 예) /css/admin_style.css → static/css/admin_style.css
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // /** 패턴: 위에서 매칭되지 않은 나머지 모든 정적 자원 요청
        // → static/ 최상위 폴더에서 파일을 찾음
        // 예) /favicon.ico → static/favicon.ico
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    // 인터셉터 설정
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor()) // 인터셉터 클래스 이름 확인!
                .addPathPatterns("/member/mypage", "/member/modify") // 로그인이 필요한 주소들 "/todo/**"
                .excludePathPatterns(
                        "/member/login",
                        "/member/join",
                        "/member/checkId",    // 우리가 만든 아이디 중복 체크 허용
                        "/member/checkEmail", // 이메일 중복 체크 허용
                        "/js/**",
                        "/css/**",
                        "/favicon.ico"
                );
    }


}

/*
 * ========== CustomServletConfig 설명 ==========
 * - 역할: 정적 리소스 핸들링 + 로그인 체크 인터셉터 설정
 *
 * [addResourceHandlers]
 * - /js/** → static/js/ 폴더
 * - /css/** → static/css/ 폴더
 * - /** → static/ 최상위 폴더 (나머지 정적 자원)
 *
 * [addInterceptors]
 * - LoginCheckInterceptor를 /member/mypage, /member/modify에 적용
 * - 로그인/회원가입/중복체크/정적자원은 인터셉터에서 제외
 */