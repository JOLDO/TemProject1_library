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
@RequiredArgsConstructor
@Log4j2
public class InfoController {

    private final InfoService infoService;

    // 1. [GET] 홈 / 자료현황
    @GetMapping("/basic")
    public String getLibraryInfo(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        model.addAttribute("stats", infoService.getLibraryStatistics());
        return "apply/basic"; // templates/apply/basic.html 호출
    }

    // 2. [GET] 조직 및 업무
    @GetMapping("/organization")
    public String organizationPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "apply/organization"; // templates/apply/organization.html 호출
    }

    // 3. [GET] 시설 현황
    @GetMapping("/facilities")
    public String facilitiesPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "apply/facilities"; // templates/apply/facilities.html 호출
    }

    // 4. [GET] 찾아오시는 길
    @GetMapping("/map")
    public String mapPage(Model model) {
        model.addAttribute("info", infoService.getStaticLibraryInfo());
        return "apply/map"; // templates/apply/map.html 호출
    }

    // 5. [GET] 등록 페이지 이동
    @GetMapping("/register")
    public String registerPage() {
        return "apply/register"; // templates/apply/register.html 호출
    }

    // --- POST 데이터 처리 로직 ---

    @PostMapping("/register")
    public String registerPOST(LibraryStatsDTO dto) {
        infoService.registerStat(dto);
        return "redirect:/info/basic";
    }

    @PostMapping("modify")
    public String modify(LibraryStatsDTO dto) {
        infoService.modifyStat(dto);
        return "redirect:/info/basic";
    }

    @PostMapping("/remove/{statId}")
    public String remove(@PathVariable("statId") Long statId) {
        infoService.removeStat(statId);
        return "redirect:/info/basic";
    }

    @GetMapping("/donation")
    public String donationGET() {
        log.info("기증 및 납본 안내 페이지 이동");
        return "apply/donation"; // 폴더명 'apply' 확인하세요!
    }
}