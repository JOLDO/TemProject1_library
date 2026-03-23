package com.library.project.library.controller;

import com.library.project.library.dto.EventApplyDTO;
import com.library.project.library.service.EventApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/mypage")

@RequiredArgsConstructor
public class MemberEventApplyController {
    private final EventApplyService applyService;

    @GetMapping("/apply-list")
    public String myApplyList(Model model) {
        // Principal principal 파라미터를 아예 지우고!
        // 테스트용 이메일을 강제로 주입해 (DB에 있는 회원 이메일 아무거나)
        String testEmail = "user1@naver.com";

        List<EventApplyDTO> applyList = applyService.getMyList(testEmail);

        model.addAttribute("applyList", applyList);
        return "member/applyList";
    }

    // 로그인한 유저의 이메일로 리스트 조회
//        List<EventApplyDTO> applyList = applyService.getMyList(principal.getName());

//        model.addAtLtribute("applyList", applyist);
//        return "mypage/applyList"; // templates/mypage/applyList.html 경로 확인!
//    }
}

/*
 * ========== MemberEventApplyController 설명 ==========
 * - 역할: 마이페이지에서 회원의 행사 신청 내역을 조회하는 컨트롤러
 * - URL 패턴: /mypage/**
 *
 * [메서드]
 * - myApplyList(): GET /mypage/apply-list → 내 행사 신청 목록 조회 (member/applyList.html)
 */
