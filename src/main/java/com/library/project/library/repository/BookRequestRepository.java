package com.library.project.library.repository;

import com.library.project.library.entity.BookRequest;
import com.library.project.library.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

    // 상태별 신청 목록 조회 (관리자용)
    List<BookRequest> findByStatus(RequestStatus status);

    // 회원별 신청 목록 조회 (회원용)
    List<BookRequest> findByMember_Id(Long memberId);

    // 특정 도서의 PENDING 신청 존재 여부 확인 (중복 신청 방지)
    boolean existsByBook_IdAndStatus(Long bookId, RequestStatus status);
}
