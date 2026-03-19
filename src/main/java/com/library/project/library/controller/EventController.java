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

@Controller
@RequestMapping("/event")
@Log4j2
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 이달의 행사 리스트 (전체 보기)
    @GetMapping("/list")
    public void list(Model model,
                     @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("이달의 행사 리스트 페이징 조회 중...");

        // 여기서도 getAll() 대신 페이징이 가능한 메서드를 쓰거나, Page를 반환받아야 해!
        // 만약 getAll()을 Pageable을 받게 수정했다면 아래처럼 써줘.
        Page<EventDTO> dtoList = eventService.getList(pageable);

        model.addAttribute("dtoList", dtoList.getContent());
        model.addAttribute("page", dtoList); // HTML에서 페이지 버튼 쓰려면 이것도 필요해!
    }

    // 행사 및 강좌 상세 안내
    @GetMapping("/read")
    public void read(Long id, Model model) {
        log.info("행사 상세 정보 조회 : " + id);
        EventDTO eventDTO = eventService.readOne(id);
        model.addAttribute("dto", eventDTO);
    }

    @GetMapping("/lecture")
    public String lectureList(Model model,
                              @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("강좌 및 세미나 리스트 페이징 조회 중...");

        Page<EventDTO> lecturePage = eventService.getLecturesByCategory("G", pageable);

        model.addAttribute("lectureList", lecturePage.getContent());
        model.addAttribute("page", lecturePage);

        return "event/lecture";
    }
}