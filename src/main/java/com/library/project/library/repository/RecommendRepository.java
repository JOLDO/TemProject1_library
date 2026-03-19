package com.library.project.library.repository;

import com.library.project.library.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

    // 특정 bookId에 대한 추천 기록이 하나라도 있는지 확인
    // → SELECT COUNT(*) > 0 FROM recommend_history WHERE book_id = ?
    boolean existsByBook_Id(Long bookId);

    // 특정 bookId에 대한 추천 기록 전부 삭제 (추천 취소 시 사용)
    // → DELETE FROM recommend_history WHERE book_id = ?
    void deleteByBook_Id(Long bookId);

    // ─────────────────────────────────────────────────────────────────
    // bookId 목록을 한 번에 받아서, 추천 기록이 있는 bookId만 반환 (배치 조회)
    //
    // [왜 이 메서드가 필요한가]
    // 리스트 화면에서 책마다 existsByBook_Id()를 따로 호출하면
    // 책 10권 = 10번의 쿼리 발생 → 성능 저하
    //
    // 대신 bookId 목록을 한 번에 넘겨서 IN 절로 한 방에 조회하면 쿼리 1번으로 해결됨
    //
    // [JPQL 설명]
    // SELECT r.book.id           : RecommendHistory 엔티티의 book 필드(연관관계)에서 id만 가져옴
    // FROM RecommendHistory r    : RecommendHistory 엔티티를 r로 별칭
    // WHERE r.book.id IN :bookIds: book_id가 전달받은 목록 안에 있는 것만 필터
    // ─────────────────────────────────────────────────────────────────
    @Query("SELECT r.book.id FROM Recommend r WHERE r.book.id IN :bookIds")
    List<Long> findBookIdsByBookIdIn(@Param("bookIds") Collection<Long> bookIds);
}