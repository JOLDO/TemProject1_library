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

    // 1. 관장 인사말 (greeting.html)
    @GetMapping("")
    public String greeting() {
        return "info/greeting";
    }

    // 2. 도서관 연혁 (history.html)
    @GetMapping("/history")
    public String history() {
        return "info/history";
    }

    // 3. 조직 및 업무 (organization.html)
    @GetMapping("/organization")
    public String organization() {
        return "info/organization";
    }

    // 4. 이용 안내 (guide.html)
    @GetMapping("/guide")
    public String guide() {
        return "info/guide";
    }

    // 5. 시설 현황 (facilities.html)
    @GetMapping("/facilities")
    public String facilities() {
        return "info/facilities";
    }

    // 6. 찾아오시는 길 (map.html)
    @GetMapping("/map")
    public String map() {
        return "info/map";
    }

    // ★ 7. 기증 및 납본 안내 (donation.html) - 새로 추가됨!
    @GetMapping("/donation")
    public String donation() {
        log.info("기증 및 납본 안내 페이지 접속...");
        return "info/donation";
    }

    // 8. 자료 현황 목록 (basic.html)
    @GetMapping("/basic")
    public String basic(Model model) {
        model.addAttribute("stats", infoService.getLibraryStatistics());
        return "info/basic";
    }

    // 9. 자료 등록 페이지
    @GetMapping("/register")
    public void registerGET() {}

    @PostMapping("/register")
    public String registerPOST(LibraryStatsDTO dto) {
        infoService.registerStat(dto);
        return "redirect:/info/basic";
    }

    // 10. 자료 수정 페이지
    @GetMapping("/modify")
    public void modifyGET(@RequestParam("id") Long id, Model model) {
        model.addAttribute("stat", infoService.getStat(id));
    }

    @PostMapping("/modify")
    public String modifyPOST(LibraryStatsDTO dto) {
        infoService.modifyStat(dto);
        return "redirect:/info/basic";
    }

    // 11. 자료 삭제 처리
    @PostMapping("/remove")
    public String removePOST(@RequestParam("statId") Long statId) {
        infoService.removeStat(statId);
        return "redirect:/info/basic";
    }
}

/*
 * ========== InfoController 설명 ==========
 * - 역할: 도서관 정보(소개) 관련 화면 + 자료 현황 CRUD를 처리하는 컨트롤러
 * - URL 패턴: /info/**
 *
 * [메서드]
 * - getLibraryInfo(): GET /info/basic → 자료현황 페이지 (도서관 정보 + 통계 데이터)
 * - organizationPage(): GET /info/organization → 조직 및 업무 페이지
 * - facilitiesPage(): GET /info/facilities → 시설 현황 페이지
 * - mapPage(): GET /info/map → 찾아오시는 길 페이지
 * - donationPage(): GET /info/donation → 기증/납본 안내 페이지
 * - registerGET(): GET /info/register → 자료 등록 페이지
 * - registerPOST(): POST /info/register → 자료 현황 항목 등록 처리
 * - modifyPOST(): POST /info/modify → 자료 현황 항목 수정 처리
 * - remove(): POST /info/remove/{id} → 자료 현황 항목 삭제 처리
 */