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
