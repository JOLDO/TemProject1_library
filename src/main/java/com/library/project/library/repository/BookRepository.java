package com.library.project.library.repository;

import com.library.project.library.entity.Book;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.repository.search.BookSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookSearch {

    // 특정 isbn을 가진 Book 중 지정한 status 상태인 것이 하나라도 있는지 확인
    // Spring Data JPA가 메서드 이름을 분석해서 자동으로 쿼리를 생성해줌
    // → SELECT COUNT(*) > 0 FROM book WHERE isbn = ? AND status = ?
    boolean existsByIsbnAndStatus(String isbn, BookStatus status);

    // ─────────────────────────────────────────────────────────────────
    // isbn 목록을 한 번에 받아서, 그 중 AVAILABLE 상태인 isbn만 반환 (배치 조회)
    //
    // [왜 이 메서드가 필요한가]
    // 리스트 화면에서 책마다 existsByIsbnAndStatus()를 따로 호출하면
    // 책 10권 = 10번의 쿼리 발생 → 성능 저하
    //
    // 대신 isbn 목록을 한 번에 넘겨서 IN 절로 한 방에 조회하면 쿼리 1번으로 해결됨
    //
    // [JPQL 설명]
    // SELECT DISTINCT b.isbn : isbn만 가져오되 중복 제거
    // FROM Book b             : Book 엔티티(= book 테이블)를 b로 별칭
    // WHERE b.isbn IN :isbns  : isbn이 전달받은 목록 안에 있는 것만 필터
    // AND b.status = :status  : 그 중에서도 status가 AVAILABLE인 것만
    // ─────────────────────────────────────────────────────────────────
    @Query("SELECT DISTINCT b.isbn FROM Book b WHERE b.isbn IN :isbns AND b.status = :status")
    List<String> findAvailableIsbnIn(@Param("isbns") Collection<String> isbns, @Param("status") BookStatus status);

    // 모달에서 isbn 기준 전체 권 목록 조회 (권별 상태 표시용) - 현재 미사용
    // List<Book> findAllByIsbn(String isbn);
    // 예약 승인 시 해당 ISBN의 AVAILABLE인 권 하나를 가져오기
    Optional<Book> findFirstByIsbnAndStatus(String isbn, BookStatus status);
}

/*
 * ========== BookRepository 설명 ==========
 * - 역할: Book 엔티티의 DB 접근을 담당하는 리포지토리
 * - 상속: JpaRepository (기본 CRUD) + BookSearch (QueryDSL 커스텀 검색)
 * - 쓰이는 곳: BookServiceImpl, RentalService에서 사용
 *
 * [메서드]
 * - existsByIsbnAndStatus(): 특정 isbn의 책 중 해당 status인 게 있는지 확인 → 단건 대여 가능 여부 체크 (BookServiceImpl.getBook())
 * - findAvailableIsbnIn(): isbn 목록을 IN 쿼리로 한 번에 조회하여 AVAILABLE인 isbn만 반환 → 목록 화면 배치 조회 최적화 (BookServiceImpl.list())
 */
