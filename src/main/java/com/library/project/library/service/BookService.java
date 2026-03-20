package com.library.project.library.service;


import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;

public interface BookService {
    // void printApiResponse();
    // boolean isReady();
    PageResponseDTO<BookDTO> list(PageRequestDTO pageRequestDTO);
    BookDTO getBook(Long bookId);
    void recommend(Long bookId);
    void unrecommend(Long bookId);
}
