package com.library.project.library.config;

import com.library.project.library.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("--- 관리자 체크 인터셉터 실행 ---");

        HttpSession session = request.getSession();
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        // 1. 로그인 안 했으면 로그인 페이지로
        if (loginInfo == null) {
            response.sendRedirect("/member/login");
            return false;
        }

        // 2. ADMIN이 아니면 메인 페이지로
        if (!"ADMIN".equals(loginInfo.getRole())) {
            log.warn("관리자 아님! 접근 차단: " + loginInfo.getMid());
            response.sendRedirect("/");
            return false;
        }

        return true;
    }
}