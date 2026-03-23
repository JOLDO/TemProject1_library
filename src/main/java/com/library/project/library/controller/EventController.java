package com.library.project.library.controller;

import com.library.project.library.dto.EventDTO;
import com.library.project.library.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/event")
@Log4j2
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 1. 이달의 행사 (캘린더) - 전체 데이터 사용
    @GetMapping("/list")
    public void list(Model model) {
        log.info("캘린더용 전체 데이터 조회...");
        List<EventDTO> dtoList = eventService.getAllEvents();
        model.addAttribute("dtoList", dtoList);
    }

    // 행사 및 강좌 상세 안내
    @GetMapping("/read")
    public void read(Long id, Model model) {
        log.info("행사 상세 정보 조회 : " + id);
        EventDTO eventDTO = eventService.readOne(id);
        model.addAttribute("dto", eventDTO);
    }

    // 2. 강좌 리스트 - 검색과 페이징 통합
    @GetMapping("/lecture")
    public String lectureList(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            @PageableDefault(size = 10, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("강좌 목록 조회 중... 검색어: " + keyword);

        // [수정 포인트] 통합된 서비스 메서드 getListWithSearch 호출! (카테고리 "G")
        Page<EventDTO> lecturePage = eventService.getListWithSearch("G", keyword, pageable);

        model.addAttribute("lectureList", lecturePage.getContent());
        model.addAttribute("page", lecturePage); // 페이징 객체
        model.addAttribute("keyword", keyword); // 검색창 유지용

        return "event/lecture";
    }

    // 3. 주말 극장 - 검색과 페이징 통합
    @GetMapping("/cinema")
    public String cinemaList(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            @PageableDefault(size = 8, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("주말 극장 목록 조회 중... 검색어: " + keyword);

        // [수정 포인트] 통합된 서비스 메서드 getListWithSearch 호출! (카테고리 "M")
        Page<EventDTO> moviePage = eventService.getListWithSearch("M", keyword, pageable);

        model.addAttribute("movieList", moviePage.getContent());
        model.addAttribute("page", moviePage);
        model.addAttribute("keyword", keyword); // 검색창 유지용

        return "event/cinema";
    }
}

/*
 * ========== EventController 설명 ==========
 * - 역할: 행사/강좌/영화상영 관련 화면 요청을 처리하는 컨트롤러
 * - URL 패턴: /event/**
 *
 * [메서드]
 * - list(): GET /event/list → 이달의 행사 캘린더 (전체 데이터 조회, event/list.html)
 * - read(): GET /event/read?id=N → 행사 상세 페이지 (event/read.html)
 * - lectureList(): GET /event/lecture → 강좌 리스트 (카테고리 "G", 페이징, event/lecture.html)
 * - cinemaList(): GET /event/cinema → 주말 극장 (카테고리 "M", 검색+페이징, event/cinema.html)
 */