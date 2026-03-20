package com.library.project.library.service;

import com.library.project.library.dto.EventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EventService {
    Long register(EventDTO eventDTO); // 등록
    EventDTO readOne(Long id);       // 상세 조회

    Page<EventDTO> getLecturesByCategory(String category, Pageable pageable);

    // [통합 목록 & 검색] 카테고리와 키워드를 한 번에 처리!
    // keyword가 null이면 전체 목록, 있으면 검색 결과가 나와.
    Page<EventDTO> getListWithSearch(String category, String keyword, Pageable pageable);

    // 기존 메서드들은 하위 호환을 위해 두거나, 필요 없으면 지워도 돼!
    Page<EventDTO> getList(Pageable pageable);
    List<EventDTO> getAllEvents();

    Page<EventDTO> getCinemaWithSearch(String keyword, Pageable pageable);

    // EventService.java 인터페이스 안에 추가!
    void applyEvent(Long eventId, String mid);
}