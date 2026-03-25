package com.library.project.library.controller;

import com.library.project.library.dto.*;
import com.library.project.library.service.NoticeService;
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

    // 1. 목록 조회 (댓글 개수 + 첨부 이미지 포함 버전으로 통합)
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        log.info("공지사항 목록 조회 (이미지/댓글포함): " + pageRequestDTO);

        // 기존 PageResponseDTO<NoticeDTO>에서 PageResponseDTO<BoardListAllDTO>로 교체
        PageResponseDTO<NoticeListAllDTO> responseDTO = noticeService.listWithAll(pageRequestDTO);

        model.addAttribute("responseDTO", responseDTO);
    }

    // 2. 등록 페이지 이동
    @GetMapping("/register")
    public void registerGet() {
        log.info("공지사항 등록 페이지 이동");
    }

    // 3. 등록 처리
    @PostMapping("/register")
    public String registerPost(@Valid NoticeDTO noticeDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/notice/register";
        }

        Long nno = noticeService.register(noticeDTO);
        redirectAttributes.addFlashAttribute("result", nno);

        return "redirect:/notice/list";
    }

    // 4. 상세 조회 및 수정 페이지 이동
    @GetMapping({"/read", "/modify"})
    public void read(Long nno, PageRequestDTO pageRequestDTO, Model model) {
        NoticeDTO noticeDTO = noticeService.readOne(nno);
        log.info("상세 조회/수정 페이지 데이터: " + noticeDTO);
        model.addAttribute("dto", noticeDTO);
    }

    // 5. 수정 처리
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid NoticeDTO noticeDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            log.info("수정 시 제약조건 오류 발생");
            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("nno", noticeDTO.getNno());
            return "redirect:/notice/modify?" + link;
        }

        noticeService.modify(noticeDTO);

        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("nno", noticeDTO.getNno());

        return "redirect:/notice/read";
    }

    // 6. 삭제 처리
    @PostMapping("/remove")
    public String remove(Long nno, RedirectAttributes redirectAttributes) {
        log.info("삭제할 번호: " + nno);
        noticeService.remove(nno);
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/notice/list";
    }
}