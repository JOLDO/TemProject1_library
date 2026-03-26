package com.library.project.library.config;

import com.library.project.library.dto.MemberDTO;
import com.library.project.library.exception.NotLoginException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
//로그인 정보 가져옴
public class SessionHelper {
    // 로그인 정보
    public MemberDTO getMemberInfo(HttpSession session) {   //비로그인, 로그인 아무때나 써도 될때
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        return loginInfo;
    }

    // 비로그인이면 예외 발생 (추천하기 등 로그인 필수인 곳에서 사용)
    public MemberDTO getRequiredMemberInfo(HttpSession session) {   //꼭 로그인 일때만 써야할때
        MemberDTO loginInfo = getMemberInfo(session);
        if (loginInfo == null) {
            throw new NotLoginException();
        }
        return loginInfo;
    }
}
