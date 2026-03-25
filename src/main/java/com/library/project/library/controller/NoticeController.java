package com.library.project.library.controller;

import com.library.project.library.dto.*;
import com.library.project.library.service.NoticeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notice")
@Log4j2
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<NoticeListAllDTO> responseDTO = noticeService.listWithAll(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
    }

    // 📍 1. 수정 페이지 진입 차단 (GET)
    @GetMapping("/modify")
    public String modifyGet(Long nno, PageRequestDTO pageRequestDTO, Model model,
                            HttpSession session, RedirectAttributes redirectAttributes) {

        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        // [디버깅 로그] - 인텔리제이 콘솔을 꼭 확인하세요!
        if (loginInfo != null) {
            log.info("📢 현재 접속 아이디: " + loginInfo.getMid());
            log.info("📢 현재 접속 권한(Role): [" + loginInfo.getRole() + "]"); // 대괄호 안의 공백 유무 확인
        } else {
            log.info("📢 현재 비로그인 상태입니다.");
        }

        // 📍 관리자 권한 체크 (대소문자 무시, 공백 제거)
        if (loginInfo == null || loginInfo.getRole() == null ||
                !"ADMIN".equalsIgnoreCase(loginInfo.getRole().trim())) {

            log.error("❌ [접근 거부] 관리자가 아니므로 목록으로 튕깁니다.");
            redirectAttributes.addFlashAttribute("error", "관리자 전용 페이지입니다.");
            return "redirect:/notice/list";
        }

        model.addAttribute("dto", noticeService.readOne(nno));
        return "notice/modify";
    }

    // 📍 2. 실제 수정 실행 차단 (POST)
    @PostMapping("/modify")
    public String modify(@Valid NoticeDTO noticeDTO, BindingResult bindingResult,
                         PageRequestDTO pageRequestDTO, HttpSession session,
                         RedirectAttributes redirectAttributes) {

        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        // 서버단 2차 방어
        if (loginInfo == null || loginInfo.getRole() == null ||
                !"ADMIN".equalsIgnoreCase(loginInfo.getRole().trim())) {
            return "redirect:/notice/list";
        }

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("nno", noticeDTO.getNno());
            return "redirect:/notice/modify?" + pageRequestDTO.getLink();
        }

        noticeService.modify(noticeDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("nno", noticeDTO.getNno());
        return "redirect:/notice/read";
    }

    // 📍 3. 삭제 차단 (POST)
    @PostMapping("/remove")
    public String remove(Long nno, HttpSession session, RedirectAttributes redirectAttributes) {

        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        if (loginInfo == null || loginInfo.getRole() == null ||
                !"ADMIN".equalsIgnoreCase(loginInfo.getRole().trim())) {
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/notice/list";
        }

        noticeService.remove(nno);
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/notice/list";
    }

    // --- 등록(Register) 및 상세조회(Read)는 기존과 동일 ---
    @GetMapping("/register")
    public String registerGet(HttpSession session, RedirectAttributes redirectAttributes) {
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        if (loginInfo == null || !"ADMIN".equalsIgnoreCase(loginInfo.getRole())) {
            return "redirect:/notice/list";
        }
        return "notice/register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid NoticeDTO noticeDTO, BindingResult bindingResult,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        if (loginInfo == null || !"ADMIN".equalsIgnoreCase(loginInfo.getRole())) {
            return "redirect:/notice/list";
        }
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/notice/register";
        }
        Long nno = noticeService.register(noticeDTO);
        redirectAttributes.addFlashAttribute("result", nno);
        return "redirect:/notice/list";
    }

    @GetMapping("/read")
    public void read(Long nno, PageRequestDTO pageRequestDTO, Model model) {
        model.addAttribute("dto", noticeService.readOne(nno));
    }
    /*@GetMapping("/modify")
    public String modifyGet(Long nno, RedirectAttributes redirectAttributes) {
        // 📍 권한이고 뭐고 무조건 목록으로 튕기게 작성
        log.info("!!!!!!!!!!!!!!!! 무조건 튕겨야 함 !!!!!!!!!!!!!!!!");
        return "redirect:/notice/list";
    }*/

}