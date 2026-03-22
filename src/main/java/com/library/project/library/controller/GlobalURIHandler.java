package com.library.project.library.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalURIHandler {

    @ModelAttribute("currentURI")
    public String currentURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

/*
 * ========== GlobalModelAdvice 설명 ==========
 * - 역할: 모든 컨트롤러에 공통 Model 속성을 자동 주입하는 @ControllerAdvice
 * - 쓰이는 곳: 모든 뷰(html)에서 currentURI 변수 사용 가능
 *
 * [메서드]
 * - currentURI(): 현재 요청 URI를 "currentURI"라는 이름으로 모든 뷰에 전달
 *   → 네비게이션 메뉴에서 현재 페이지 활성화 표시 등에 사용
 */