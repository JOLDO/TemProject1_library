package com.library.project.library.controller;

import com.library.project.library.dto.BookRequestDto.BookRequestDTO;
import com.library.project.library.dto.BookRequestDto.BookRequestResponseDTO;
import com.library.project.library.service.BookRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requests")
public class BookRequestController {

    private final BookRequestService bookRequestService;

    /** 대출 신청 (회원) POST /api/requests */
    @PostMapping
    public ResponseEntity<String> requestBook(@RequestBody BookRequestDTO dto) {
        bookRequestService.requestBook(dto);
        return ResponseEntity.ok("신청 완료");
    }

    /** 신청 목록 조회 - PENDING (관리자) GET /api/requests */
    @GetMapping
    public ResponseEntity<List<BookRequestResponseDTO>> getPendingRequests() {
        return ResponseEntity.ok(bookRequestService.getPendingRequests());
    }

    /** 신청 승인 (관리자) POST /api/requests/{id}/approve */
    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        bookRequestService.approveRequest(id);
        return ResponseEntity.ok("승인 완료");
    }

    /** 신청 거절 (관리자) POST /api/requests/{id}/reject */
    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        bookRequestService.rejectRequest(id);
        return ResponseEntity.ok("거절 완료");
    }

    /** 회원별 신청 목록 (회원) GET /api/requests/member/{memberId} */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BookRequestResponseDTO>> getMyRequests(@PathVariable Long memberId) {
        return ResponseEntity.ok(bookRequestService.getMyRequests(memberId));
    }

}