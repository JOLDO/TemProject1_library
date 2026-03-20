package com.library.project.library.controller;

import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

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
    public BookDTO getBook(@PathVariable Long bookId) {
        return bookService.getBook(bookId);
    }

    // =============================================
    // 책 목록 조회 (리스트 페이지 axios용) 추가된거
    // =============================================
    @GetMapping("/book/list")
    public PageResponseDTO<BookDTO> getBookList(PageRequestDTO pageRequestDTO) {
        return bookService.list(pageRequestDTO);
    }

    // =============================================
    // 책 권별 상태 목록 조회 (모달용)
    // 클릭한 책의 isbn 기준으로 전체 권 목록과 각 권의 status 반환
    // 예) isbn=978... → [{id:1, status:RENTED}, {id:2, status:AVAILABLE}, ...]
    // =============================================
    /*@GetMapping("/book/{bookId}/copies")
    public List<BookDTO> getCopies(@PathVariable Long bookId) {
        return bookService.getCopiesByBookId(bookId);
    }*/

    // =============================================
    // 추천하기
    // bookId: 대표 row id → RecommendHistory에 row 추가
    // =============================================
    @PostMapping("/book/recommend/{bookId}")
    public void recommend(@PathVariable Long bookId) {
        bookService.recommend(bookId);
    }

    // =============================================
    // 추천 해제
    // bookId: 대표 row id → RecommendHistory에서 row 삭제
    // =============================================
    @DeleteMapping("/book/recommend/{bookId}")
    public void unrecommend(@PathVariable Long bookId) {
        bookService.unrecommend(bookId);
    }
}