package com.library.project.library.repository;

import com.library.project.library.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    // 특정 회원이 특정 책을 추천했는지 확인 (추천 버튼 초기 상태용)
    boolean existsByBook_IdAndMember_Id(Long bookId, Long memberId);

    // 특정 회원의 특정 책 추천 삭제 (추천 해제 시 사용)
    void deleteByBook_IdAndMember_Id(Long bookId, Long memberId);

    // ─────────────────────────────────────────────────────────────────
    // 특정 회원이 추천한 bookId만 배치 조회 (목록 화면 최적화)
    // 책마다 existsBy~를 호출하면 N번 쿼리 → IN 절로 1번에 해결
    // ─────────────────────────────────────────────────────────────────
    @Query("SELECT r.book.id FROM Recommend r WHERE r.book.id IN :bookIds AND r.member.id = :memberId")
    List<Long> findBookIdsByBookIdIn(@Param("bookIds") Collection<Long> bookIds, @Param("memberId") Long memberId);

    // 특정 책의 총 추천 수 집계
    int countByBook_Id(Long bookId);
}

/*
 * ========== RecommendRepository 설명 ==========
 * - 역할: Recommend 엔티티의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: BookServiceImpl에서 사용
 *
 * [메서드]
 * - existsByBook_IdAndMember_Id(): 특정 회원이 특정 책을 추천했는지 확인 → 단건 조회 시 추천 버튼 상태
 * - deleteByBook_IdAndMember_Id(): 특정 회원의 특정 책 추천만 삭제 → 추천 해제
 * - findBookIdsByBookIdIn(): 특정 회원이 추천한 bookId를 IN 쿼리로 배치 조회 → 목록 화면 최적화
 * - countByBook_Id(): 특정 책의 총 추천 수 집계 → 추천 수 표시
 */