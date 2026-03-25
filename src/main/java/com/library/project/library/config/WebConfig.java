package com.library.project.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 회원용
        registry.addViewController("/user_rentals").setViewName("forward:/user_rentals.html");
        // 관리자용
        registry.addViewController("/rentals").setViewName("forward:/rentals.html");
    }



}

/*
 * ========== WebConfig 설명 ==========
 * - 역할: 간단한 URL → View 매핑을 설정하는 클래스 (컨트롤러 없이 뷰 연결)
 *
 * [설정 내용]
 * - /user_rentals → user_rentals.html (회원용 대출 현황)
 * - /rentals → rentals.html (관리자용 대출 관리)
 * - 비즈니스 로직 없이 단순 페이지 이동만 필요할 때 사용
 */
