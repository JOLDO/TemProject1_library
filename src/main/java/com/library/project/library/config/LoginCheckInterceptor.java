package com.library.project.library.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.HandlerInterceptor;

@Log4j2
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("--- 로그인 체크 인터셉터 실행 ---");

        // ✅ 캐시 금지 (뒤로가기 방지)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 2. 세션 가져오기
        HttpSession session = request.getSession(false); // ✅ false: 새 세션 생성 안 함

        log.info("세션 ID: " + (session != null ? session.getId() : "없음"));
        log.info("세션 만료시간(초): " + (session != null ? session.getMaxInactiveInterval() : "없음"));
        log.info("loginInfo: " + (session != null ? session.getAttribute("loginInfo") : "없음"));

        // 3. 로그인 체크
        if (session == null || session.getAttribute("loginInfo") == null) {
            log.info("로그인 정보 없음! 로그인 페이지로 이동합니다.");

            // 4. dest 저장
            /***핵심 기능이에요!**
                    예를 들어 로그인 안 한 상태에서 `/member/mypage` 접근하면:
            dest = "/member/mypage" 저장
                    ↓
            로그인 페이지로 이동
                    ↓
            로그인 성공
                    ↓
            저장해둔 dest("/member/mypage") 로 자동 이동!*/
            // ✅ 원래 가려던 주소 저장 (로그인 후 돌아오기 위해)
            HttpSession newSession = request.getSession(true);
            String dest = request.getRequestURI() +
                    (request.getQueryString() != null ? "?" + request.getQueryString() : "");

            // 5. 필터링 조건
            if (!dest.contains(".css") && !dest.contains(".js") && !dest.contains(".ico")
                    && !dest.contains(".png") && !dest.contains(".jpg")
                    && !dest.equals("/member/login")
                    && !dest.equals("/member/join")
                    && !dest.equals("/")) {
                newSession.setAttribute("dest", dest);
            }

            response.sendRedirect("/member/login");
            return false;
        }

        return true;
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