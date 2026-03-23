package com.library.project.library.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
public class LoginCheckInterceptor_bk20260323 implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("--- 로그인 체크 인터셉터 실행 ---");

        HttpSession session = request.getSession();

        // 세션에 loginInfo(로그인 정보)가 없으면 로그인 페이지로 튕겨내기
        if (session.getAttribute("loginInfo") == null) {
            log.info("로그인 정보 없음! 로그인 페이지로 이동합니다.");
            response.sendRedirect("/member/login");
            return false; // 컨트롤러로 못 가게 막음
        }

        return true; // 로그인 되어 있으면 통과!
    }
}

/*
 * ========== LoginCheckInterceptor 설명 ==========
 * - 역할: 로그인이 필요한 페이지 접근 시 세션 체크를 수행하는 인터셉터
 * - 등록 위치: CustomServletConfig.addInterceptors()에서 등록
 * - 적용 경로: /member/mypage, /member/modify
 *
 * [preHandle() 동작]
 * - 세션에 "loginInfo"가 없으면 → /member/login으로 리다이렉트 (false 반환, 컨트롤러 진입 차단)
 * - 세션에 "loginInfo"가 있으면 → 통과 (true 반환, 컨트롤러 정상 진입)
 */