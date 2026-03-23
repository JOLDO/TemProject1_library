package com.library.project.library.repository;

import com.library.project.library.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    // [통합 검색 메서드] 특정 카테고리 내에서 제목으로 검색 + 페이징
    // 강좌(G), 영화(M) 호출 시 카테고리만 바꿔서 던지면 됨!
    Page<Event> findByCategoryAndTitleContaining(String category, String title, Pageable pageable);

    // [기본 목록 메서드] 검색어 없을 때 특정 카테고리 전체 페이징
    Page<Event> findByCategory(String category, Pageable pageable);

    // [참고] 혹시 특정 카테고리 제외가 필요할 때 사용 (기존 코드 유지)
    Page<Event> findByCategoryNot(String category, Pageable pageable);
}

/*
 * ========== EventRepository 설명 ==========
 * - 역할: Event 엔티티의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: EventServiceImpl에서 사용
 *
 * [메서드]
 * - findByCategory(): 카테고리별 행사 목록 페이징 조회 → 강좌(G) 또는 영화(M) 목록
 * - findByTitleContainingAndCategory(): 제목 검색 + 카테고리 필터 → 강좌 검색
 * - findByCategoryNot(): 특정 카테고리 제외 조회 → 영화(M) 제외한 행사 목록
 * - findByCategoryAndTitleContaining(): 카테고리 고정 + 제목 검색 → 주말 극장 영화 검색
 */
