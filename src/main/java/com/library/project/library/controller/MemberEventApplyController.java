package com.library.project.library.controller;

import com.library.project.library.dto.EventApplyDTO;
import com.library.project.library.service.EventApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberEventApplyController {

    private final EventApplyService applyService;

    @GetMapping("/apply-list")

    public String myApplyList(Model model, Principal principal) {

        // 1. 로그인 여부 확인 (Security 설정에 따라 다르지만 안전하게 체크)
        if (principal == null) {
            log.info("로그인 정보가 없습니다. 로그인 페이지로 리다이렉트합니다.");
            return "redirect:/member/login";
        }

        // 2. 현재 로그인한 유저의 이메일(ID) 가져오기
        String email = principal.getName();
        log.info("조회 요청 회원 이메일: " + email);

        // 3. 해당 이메일로 신청 내역 서비스 호출
        List<EventApplyDTO> applyList = applyService.getMyList(email);

        // 4. 모델에 담아서 뷰로 전달
        model.addAttribute("applyList", applyList);

        return "member/applyList";
    }
}