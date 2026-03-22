package com.library.project.library.repository;

import com.library.project.library.entity.WishBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishBookRepository extends JpaRepository<WishBookEntity, Long> {
    // 기본 save(), findAll(), findById() 등은 자동으로 생성됩니다.
}

/*
 * ========== WishBookRepository 설명 ==========
 * - 역할: WishBookEntity(비치희망도서 신청)의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: WishBookServiceImpl에서 사용 (신청서 저장)
 */