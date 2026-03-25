package com.library.project.library.controller;


import com.library.project.library.dto.LibraryStatsDTO;
import com.library.project.library.service.InfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/info")
@Log4j2
@RequiredArgsConstructor
public class InfoController {

    private final InfoService infoService;

    // 1. 도서관 안내 메인 (인사말)
    @GetMapping("")
    public String greeting(Model model) {
        log.info("인사말 페이지 접속...");
        // 파일명이 greeting.html 이므로 경로를 맞춰줍니다.
        return "info/greeting";
    }

    // 2. 도서관 연혁
    @GetMapping("/history")
    public String history() {
        return "info/history";
    }

    // 3. 조직 및 업무
    @GetMapping("/organization")
    public String organization() {
        return "info/organization";
    }

    // 4. 이용 안내
    @GetMapping("/guide")
    public String guide() {
        return "info/guide";
    }

    // 5. 시설 현황
    @GetMapping("/facilities")
    public String facilities() {
        return "info/facilities";
    }

    // 6. 찾아오시는 길 (구글 지도 포함)
    @GetMapping("/map")
    public String map() {
        return "info/map";
    }

    // 7. 자료 현황 목록 (CRUD 메인)
    @GetMapping("/basic")
    public String basic(Model model) {
        model.addAttribute("stats", infoService.getLibraryStatistics());
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "info/basic";
    }

    // 8. 자료 등록/수정/삭제 로직
    @GetMapping("/register")
    public void registerGET() {}

    @PostMapping("/register")
    public String registerPOST(LibraryStatsDTO dto) {
        infoService.registerStat(dto);
        return "redirect:/info/basic";
    }

    @GetMapping("/modify")
    public void modifyGET(@RequestParam("id") Long id, Model model) {
        model.addAttribute("stat", infoService.getStat(id));
    }

    @PostMapping("/modify")
    public String modifyPOST(LibraryStatsDTO dto) {
        infoService.modifyStat(dto);
        return "redirect:/info/basic";
    }

    @PostMapping("/remove")
    public String removePOST(@RequestParam("statId") Long statId) {
        infoService.removeStat(statId);
        return "redirect:/info/basic";
    }
}