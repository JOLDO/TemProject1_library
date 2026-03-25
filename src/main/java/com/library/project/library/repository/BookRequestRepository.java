package com.library.project.library.repository;

import com.library.project.library.entity.Book;
import com.library.project.library.entity.BookRequest;
import com.library.project.library.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    // 상태별 신청 목록 조회 (관리자용)
    List<BookRequest> findByStatus(RequestStatus status);

    // 회원별 신청 목록 조회 (회원용)
    List<BookRequest> findByMember_Id(Long memberId);

    // 특정 도서의 PENDING 신청 존재 여부 확인 (중복 신청 방지)
    boolean existsByBook_IdAndStatus(Long bookId, RequestStatus status);

    // ─────────────────────────────────────────────────────────────────
    // isbn 기준 + 회원별 PENDING 신청 존재 여부 확인 (같은 책 여러 권 대응)
    //
    // [용도] BookRequestServiceImpl.requestBook()에서 중복 신청 방지에 사용
    //        기존 existsByBook_IdAndStatus()는 특정 book_id만 체크했지만,
    //        같은 isbn의 다른 book_id로 예약이 걸려있어도 중복으로 판단해야 하므로
    //        isbn + memberId 기준으로 체크
    // ─────────────────────────────────────────────────────────────────
    boolean existsByBook_IsbnAndMember_IdAndStatus(String isbn, Long memberId, RequestStatus status);

    boolean existsByMember_IdAndBook_IdAndStatus(Long memberId, Long bookId, RequestStatus status);

    @Query("SELECT br.book.isbn FROM BookRequest br WHERE br.member.id = :memberId AND br.book.isbn IN :bookIsbns AND br.status = :status")
    List<String> findBookIsbnsByMemberIdAndBookIsbnInAndStatus(@Param("memberId")Long memberId, @Param("bookIsbns")List<String> bookIsbns, @Param("status")RequestStatus status);

    // ─────────────────────────────────────────────────────────────────
    // 같은 isbn의 book_id별 예약 우선순위 조회 (예약 배정용)
    //
    // [용도] BookRequestServiceImpl.requestBook()에서 예약할 권을 결정할 때 사용
    //
    // [우선순위]
    // 1순위: AVAILABLE + 예약 0건 → 바로 빌릴 수 있는 책
    // 2순위: AVAILABLE + 예약 있음 → 빌릴 수 있지만 대기자 있는 책 (예약 적은 순)
    // 3순위: RENTED → 반납 대기 (예약 적은 순)
    //
    // [정렬 방식]
    // CASE WHEN b.status = 'AVAILABLE' THEN 0 ELSE 1 → AVAILABLE 우선
    // COUNT(br) ASC → 예약 적은 순
    // b.id ASC → 동률이면 낮은 id (일관된 배정)
    //
    // [결과 형식] List<Object[]> → Object[0]: book_id(Long), Object[1]: count(Long)
    //
    // [예시] id 1(RENTED, 예약 0건), id 2(AVAILABLE, 예약 0건) → id 2에 배정
    //        id 1(AVAILABLE, 예약 2건), id 2(AVAILABLE, 예약 0건) → id 2에 배정
    // ─────────────────────────────────────────────────────────────────
    @Query("""
        SELECT b.id, COUNT(br)
        FROM Book b
        LEFT JOIN BookRequest br ON br.book.id = b.id AND br.status = :status
        WHERE b.isbn = :isbn
        GROUP BY b.id, b.status
        ORDER BY CASE WHEN b.status = 'AVAILABLE' THEN 0 ELSE 1 END ASC,
                 COUNT(br) ASC,
                 b.id ASC
    """)
    List<Object[]> countPendingPerBookByIsbn(@Param("isbn") String isbn, @Param("status") RequestStatus status);

    // 회원의 특정 ISBN 도서 PENDING 예약 삭제
    @Modifying
    @Query("DELETE FROM BookRequest br WHERE br.member.id = :memberId AND br.book.isbn = :isbn AND br.status = :status")
    void deleteByMemberIdAndIsbnAndStatus(@Param("memberId") Long memberId, @Param("isbn") String isbn, @Param("status") RequestStatus status);

}
