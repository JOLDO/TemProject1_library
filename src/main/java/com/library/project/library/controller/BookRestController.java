package com.library.project.library.controller;

import com.library.project.library.config.SessionHelper;
import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.MemberDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.service.BookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;
    private final SessionHelper sessionHelper;

    // =============================================
    // API 로딩 완료 여부 확인
    // 앱 시작 시 네이버 API 호출이 끝났는지 로딩 화면에서 2초마다 폴링
    // true 반환 시 booklist 페이지로 이동
    // =============================================
    /*@GetMapping("/api/ready")
    public boolean checkReady() {
        return bookService.isReady();
    }*/

    // =============================================
    // 책 상세정보 조회 (상세보기 모달용)
    // =============================================
    @GetMapping("/book/{bookId}")
    public BookDTO getBook(@PathVariable Long bookId, HttpSession session) {
        MemberDTO memberInfo = sessionHelper.getMemberInfo(session);
        return bookService.getBook(bookId, memberInfo == null ? null : memberInfo.getId());
    }

    // =============================================
    // 추천하기
    // bookId: 대표 row id → RecommendHistory에 row 추가
    // =============================================
    @PostMapping("/book/recommend/{bookId}")
    public void recommend(@PathVariable Long bookId, HttpSession session) {
        bookService.recommend(bookId, sessionHelper.getRequiredMemberInfo(session).getId());
    }

    // =============================================
    // 추천 해제
    // bookId: 대표 row id → RecommendHistory에서 row 삭제
    // =============================================
    @DeleteMapping("/book/recommend/{bookId}")
    public void unrecommend(@PathVariable Long bookId, HttpSession session) {
        bookService.unrecommend(bookId, sessionHelper.getRequiredMemberInfo(session).getId());
    }
}

/*
 * ========== BookRestController 설명 ==========
 * - 역할: 도서 관련 REST API 요청을 처리하는 컨트롤러 (JSON 응답)
 * - 쓰이는 곳: 프론트엔드 JavaScript(axios)에서 AJAX 호출
 *
 * [메서드]
 * - getBook(): GET /book/{bookId} → 도서 단건 상세 조회 (상세보기 모달용)
 * - getBookList(): GET /book/list → 도서 목록 JSON 조회 (axios 페이징용)
 * - recommend(): POST /book/recommend/{bookId} → 추천하기 (Recommend 테이블에 row 추가)
 * - unrecommend(): DELETE /book/recommend/{bookId} → 추천 해제 (Recommend 테이블에서 row 삭제)
 */