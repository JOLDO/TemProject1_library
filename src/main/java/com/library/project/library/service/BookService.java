package com.library.project.library.service;


import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;

public interface BookService {
    // void printApiResponse();
    // boolean isReady();
    PageResponseDTO<BookDTO> list(PageRequestDTO pageRequestDTO, Long memberId);
    BookDTO getBook(Long bookId, Long memberId);
    void recommend(Long bookId, Long memberId);
    void unrecommend(Long bookId, Long memberId);
}

/*
 * ========== BookService 설명 ==========
 * - 역할: 도서 관련 비즈니스 로직 인터페이스
 * - 구현체: BookServiceImpl
 * - 쓰이는 곳: BookController, BookRestController에서 주입받아 사용
 *
 * [메서드]
 * - list(): 도서 목록 페이징 조회 (isbn 중복 제거 + 검색 + 정렬) → booklist 페이지
 * - getBook(): 도서 단건 상세 조회 → 상세보기 모달
 * - recommend(): 도서 추천하기 → 추천 버튼 클릭 시
 * - unrecommend(): 도서 추천 해제 → 추천 해제 버튼 클릭 시
 */
