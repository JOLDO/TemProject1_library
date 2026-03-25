package com.library.project.library.repository;


import com.library.project.library.entity.Rental;
import com.library.project.library.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByMember_IdAndStatus(Long MemberId, RentalStatus status);

    Optional<Rental> findByBook_IdAndStatus(Long Id, RentalStatus status);

    @Query("""
    SELECT r.book.bookTitle, COUNT(r)
    FROM Rental r
    GROUP BY r.book.bookTitle
    ORDER BY COUNT(r) DESC
""")
    List<Object[]> findMostRentedBooks();

    // 회원이 현재 대여중인 isbn 목록 (배치 조회용)
    @Query("""
        SELECT r.book.isbn
        FROM Rental r
        WHERE r.member.id = :memberId
        AND r.book.isbn IN :isbns
        AND r.status = :status
    """)
    List<String> findRentedIsbnsByMemberIdAndIsbnIn(Long memberId, List<String> isbns, RentalStatus status);

    // 오늘 대출 개수
    @Query("""
        SELECT COUNT(r)
        FROM Rental r
        WHERE r.member.id = :memberId
        AND r.rentalDate = :today
    """)
    int countTodayRentals(Long memberId, LocalDate today);


}

/*
 * ========== RentalRepository 설명 ==========
 * - 역할: Rental 엔티티의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: RentalService에서 사용
 *
 * [메서드]
 * - findByMember_IdAndStatus(): 특정 회원의 특정 상태 대출 목록 조회 → 내 대출 현황 (RENTED만)
 * - findByBook_IdAndStatus(): 특정 도서의 특정 상태 대출 조회 → 이미 대출된 책인지 중복 체크
 * - findMostRentedBooks(): 도서별 대출 횟수 집계 (내림차순) → 인기 도서 통계
 * - countTodayRentals(): 특정 회원의 오늘 대출 횟수 → 하루 3권 제한 체크
 */
