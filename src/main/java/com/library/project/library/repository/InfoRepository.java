package com.library.project.library.repository;


import com.library.project.library.entity.LibraryInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// <사용할 엔티티, PK의 타입>을 정확히 명시해야 합니다.
public interface InfoRepository extends JpaRepository<LibraryInfoEntity, Long> {
}

/*
 * ========== InfoRepository 설명 ==========
 * - 역할: LibraryInfoEntity(도서관 기본 정보)의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: InfoServiceImpl에서 사용 (findById(1L)로 도서관 정보 조회)
 */
