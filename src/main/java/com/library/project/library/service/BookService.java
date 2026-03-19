package com.library.project.library.service;


import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;

public interface BookService {
    // void printApiResponse();
    // boolean isReady();
    PageResponseDTO<BookDTO> list(PageRequestDTO pageRequestDTO);
    // BookDTO getBook(Long bookId);
    void recommend(Long bookId);
    void unrecommend(Long bookId);
    // isbn 기준 전체 권 목록 (모달에서 권별 상태 표시용) - bookId로 isbn 조회 후 전체 권 반환
    // List<BookDTO> getCopiesByBookId(Long bookId);
    // 대여: isbn 중 AVAILABLE 권 하나 → RENTED + RentalHistory 저장
    // void rental(Long bookId);
    // 반납: 해당 권 → AVAILABLE + RentalHistory 반납일 업데이트
    // void returnBook(Long bookId);
}
