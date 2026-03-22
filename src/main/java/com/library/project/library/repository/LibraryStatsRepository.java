package com.library.project.library.repository;


import com.library.project.library.entity.LibraryStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryStatsRepository extends JpaRepository<LibraryStatsEntity, Long> {
    // 상속만으로 findAll(), save(), findById() 등을 바로 사용할 수 있습니다.
}

/*
 * ========== LibraryStatsRepository 설명 ==========
 * - 역할: LibraryStatsEntity(도서관 자료 현황 통계)의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: InfoServiceImpl에서 사용 (통계 CRUD: 등록/수정/삭제/전체조회)
 */
