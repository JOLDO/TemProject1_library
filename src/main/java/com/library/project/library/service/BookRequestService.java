package com.library.project.library.service;

import com.library.project.library.dto.BookRequestDto.BookRequestDTO;
import com.library.project.library.dto.BookRequestDto.BookRequestResponseDTO;

import java.util.List;

public interface BookRequestService {

    // 대출 신청 (회원)
    void requestBook(BookRequestDTO dto);

    // 신청 목록 조회 (관리자 - PENDING 목록)
    List<BookRequestResponseDTO> getPendingRequests();

    // 신청 승인 (관리자)
    void approveRequest(Long requestId);

    // 신청 거절 (관리자)
    void rejectRequest(Long requestId);

    // 회원별 신청 목록 조회
    List<BookRequestResponseDTO> getMyRequests(Long memberId);
}
