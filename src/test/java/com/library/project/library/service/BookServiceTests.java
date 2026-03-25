package com.library.project.library.service;

import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.entity.Book;
import com.library.project.library.entity.Member;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.repository.BookRepository;
import com.library.project.library.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Log4j2
@Transactional
class BookServiceTests {

    @Autowired private BookService bookService;

    @Test
    @DisplayName("도서 목록 조회 - 비로그인")
    void list_notLoggedInTest() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResponseDTO<BookDTO> result = bookService.list(pageRequestDTO, null);

        result.getDtoList().stream().forEach(dto ->
                log.info("도서 목록 조회 - 비로그인 : " + result));
    }

    @Test
    @DisplayName("도서 목록 조회 - 로그인")
    void list_loggedInTest() {
        Long memberId = 1L;
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResponseDTO<BookDTO> result = bookService.list(pageRequestDTO, memberId);

        result.getDtoList().stream().forEach(dto ->
                log.info("도서 목록 조회 - 로그인 : " + dto));
    }

    @Test
    @DisplayName("도서 목록 검색")
    void listSearchTest() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1).size(10).keyword("ㄴㄹ").build();
        PageResponseDTO<BookDTO> result = bookService.list(pageRequestDTO, null);

        result.getDtoList().stream().forEach(dto ->
                log.info("도서 목록 검색 : " + dto));
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("도서 단건 조회 - 비로그인")
    void getBook_notLoggedInTest() {
        Long bookId = 676L;
        BookDTO dto = bookService.getBook(bookId, null);
        log.info("도서 단건 조회 - 비로그인 : " + dto);
    }

    @Test
    @DisplayName("도서 단건 조회 - 로그인")
    void getBook_loggedInTest() {
        Long bookId = 676L;
        Long memberId = 1L;
        BookDTO dto = bookService.getBook(bookId, memberId);
        log.info("도서 단건 조회 - 로그인 : " + dto);
    }

    @Test
    @DisplayName("추천 등록 후 단건 조회 시 recommended = true")
    void recommendTest() {
        Long bookId = 676L;
        Long memberId = 1L;
        bookService.recommend(bookId, memberId);

        BookDTO dto = bookService.getBook(bookId, memberId);
        log.info("추천 등록 후 단건 조회 시 : " + dto.getRecommended());
    }

    @Test
    @DisplayName("추천 해제 후 단건 조회 시 recommended = false")
    void unrecommendTest() {
        Long bookId = 676L;
        Long memberId = 1L;
        bookService.unrecommend(bookId, memberId);

        BookDTO dto = bookService.getBook(bookId, memberId);
        log.info("추천 해제 후 단건 조회 시 : " + dto.getRecommended());
    }
}