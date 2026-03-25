package com.library.project.library.repository;

import com.library.project.library.domain.Notice;
import com.library.project.library.repository.search.NoticeSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeSearch {

    // 📍 1. 이미지(imageSet)를 한 번에 조인해서 가져오는 메서드
    // attributePaths에 엔티티 내부의 이미지 리스트 필드명을 적어줍니다.
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select n from Notice n where n.nno = :nno")
    Optional<Notice> findByIdWithImages(@Param("nno") Long nno);
}