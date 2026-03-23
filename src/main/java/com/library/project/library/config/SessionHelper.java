package com.library.project.library.config;

import com.library.project.library.dto.MemberDTO;
import com.library.project.library.exception.NotLoginException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionHelper {
    // 로그인 정보
    public MemberDTO getMemberInfo(HttpSession session) {
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        return loginInfo;
    }

    // 비로그인이면 예외 발생 (추천하기 등 로그인 필수인 곳에서 사용)
    public MemberDTO getRequiredMemberInfo(HttpSession session) {
        MemberDTO loginInfo = getMemberInfo(session);
        if (loginInfo == null) {
            throw new NotLoginException();
        }
        return loginInfo;
    }
}
