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

    // 1. [GET] 홈 / 자료현황
    @GetMapping("/basic")
    public String getLibraryInfo(Model model) {
        log.info("자료현황 페이지 이동...");
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        model.addAttribute("stats", infoService.getLibraryStatistics());
        return "info/basic"; // templates/info/basic.html 호출
    }

    // 2. [GET] 조직 및 업무
    @GetMapping("/organization")
    public String organizationPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "info/organization"; // templates/info/organization.html 호출
    }

    // 3. [GET] 시설 현황
    @GetMapping("/facilities")
    public String facilitiesPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "info/facilities"; // templates/info/facilities.html 호출
    }

    // 4. [GET] 찾아오시는 길
    @GetMapping("/map")
    public String mapPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "info/map"; // templates/info/map.html 호출
    }

    // 5. [GET] 기증·납본 안내
    @GetMapping("/donation")
    public String donationPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "info/donation"; // templates/info/donation.html 호출
    }

    // 6. [GET] 등록 페이지 이동
    @GetMapping("/register")
    public String registerGET() {
        return "info/register"; // templates/info/register.html 호출
    }

    // 7. [POST] 자료 등록 실행
    @PostMapping("/register")
    public String registerPOST(LibraryStatsDTO dto) {
        infoService.registerStat(dto);
        return "redirect:/info/basic";
    }

    // 8. [POST] 자료 수정 실행 (basic.html에서 바로 호출)
    @PostMapping("/modify")
    public String modifyPOST(LibraryStatsDTO dto) {
        infoService.modifyStat(dto);
        return "redirect:/info/basic";
    }

    // 9. [POST] 자료 삭제 실행
    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        log.info("삭제 ID: " + id);
        infoService.removeStat(id);
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