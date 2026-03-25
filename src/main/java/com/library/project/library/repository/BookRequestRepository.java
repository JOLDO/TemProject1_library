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

    boolean existsByMember_IdAndBook_IdAndStatus(Long memberId, Long bookId, RequestStatus status);

    @Query("SELECT br.book.isbn FROM BookRequest br WHERE br.member.id = :memberId AND br.book.isbn IN :bookIsbns AND br.status = :status")
    List<String> findBookIsbnsByMemberIdAndBookIsbnInAndStatus(@Param("memberId")Long memberId, @Param("bookIsbns")List<String> bookIsbns, @Param("status")RequestStatus status);

    // 회원의 특정 ISBN 도서 PENDING 예약 삭제
    @Modifying
    @Query("DELETE FROM BookRequest br WHERE br.member.id = :memberId AND br.book.isbn = :isbn AND br.status = :status")
    void deleteByMemberIdAndIsbnAndStatus(@Param("memberId") Long memberId, @Param("isbn") String isbn, @Param("status") RequestStatus status);

}

