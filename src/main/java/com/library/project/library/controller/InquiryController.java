package com.library.project.library.controller;

import com.library.project.library.dto.*;
import com.library.project.library.service.InquiryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inquiry")
@Log4j2
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    // 1. 전체 리스트
    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<InquiryListReplyCountDTO> responseDTO = inquiryService.listWithReplyCount(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "inquiry/list";
    }

    // 2. 문의 등록 페이지 이동
    @GetMapping("/register")
    public String registerGet(HttpSession session, Model model) {
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        if (loginInfo == null) {
            log.warn(">>>> [로그인 체크] 로그인 정보 없음 - 로그인 페이지로 리다이렉트");
            return "redirect:/member/login?redirectURL=/inquiry/register";
        }

        // 화면에 로그인한 사용자의 mid를 보여주기 위해 전달
        model.addAttribute("mid", loginInfo.getMid());
        return "inquiry/register";
    }

    // 3. 실제 DB 등록 처리
    @PostMapping("/register")
    public String registerPost(@Valid InquiryDTO inquiryDTO,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        log.info(">>>> [컨트롤러] 문의사항 등록 POST 처리 시작");

        // 📍 세션에서 로그인 정보 확인
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        if (loginInfo == null) {
            log.error(">>>> [에러] 세션 만료 또는 로그인 정보 없음");
            return "redirect:/member/login";
        }

        // 📍 [핵심] 세션에서 꺼낸 '검증된' mid를 DTO에 강제로 세팅합니다.
        // 이 값이 서비스의 memberRepository.findByMid()로 전달되어 Transient 에러를 막습니다.
        log.info(">>>> [세션 정보 주입] 로그인 된 아이디: " + loginInfo.getMid());
        inquiryDTO.setMid(loginInfo.getMid());

        // 유효성 검사 에러 처리
        if(bindingResult.hasErrors()) {
            log.info(">>>> [에러] 유효성 검사 실패");
            // 에러가 있을 경우 입력 데이터를 다시 보냄 (redirect 대신 return)
            model.addAttribute("mid", loginInfo.getMid());
            return "inquiry/register";
        }

        // 서비스 호출
        try {
            Long ino = inquiryService.register(inquiryDTO);
            log.info(">>>> [등록 완료] 생성된 번호: " + ino);
            redirectAttributes.addFlashAttribute("result", "registered");
        } catch (Exception e) {
            log.error(">>>> [서비스 에러] " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/inquiry/register";
        }

        return "redirect:/inquiry/list";
    }

    // 4. 조회/수정 페이지
    @GetMapping({"/read", "/modify"})
    public void read(@RequestParam("ino") Long ino, PageRequestDTO pageRequestDTO, Model model) {
        InquiryDTO inquiryDTO = inquiryService.readOne(ino);
        model.addAttribute("dto", inquiryDTO);
    }

    // 5. 수정
    @PostMapping("/modify")
    public String modify(@Valid InquiryDTO inquiryDTO,
                         BindingResult bindingResult,
                         PageRequestDTO pageRequestDTO,
                         RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("ino", inquiryDTO.getIno());
            return "redirect:/inquiry/modify?" + pageRequestDTO.getLink();
        }

        inquiryService.modify(inquiryDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("ino", inquiryDTO.getIno());
        return "redirect:/inquiry/read";
    }

    // 6. 삭제
    @PostMapping("/remove")
    public String remove(@RequestParam("ino") Long ino, RedirectAttributes redirectAttributes) {
        inquiryService.remove(ino);
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/inquiry/list";
    }

    // 7. 내 문의 내역 보기
    @GetMapping("/myList")
    public String myList(PageRequestDTO pageRequestDTO, Model model, HttpSession session) {
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        if (loginInfo == null) {
            return "redirect:/member/login?redirectURL=/inquiry/myList";
        }

        String mid = loginInfo.getMid();
        PageResponseDTO<InquiryListReplyCountDTO> responseDTO =
                inquiryService.getMyInquiryList(mid, pageRequestDTO);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("mname", loginInfo.getMname());

        return "inquiry/myList";
    }
}