package com.library.project.library.repository;

import com.library.project.library.entity.Book;
import com.library.project.library.enums.BookStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Log4j2
@Transactional
class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("ISBN + 상태로 존재 여부 확인 - AVAILABLE")
    void existsByIsbnAndStatus_available() {
        String isbn = "9791136299345";
        boolean result = bookRepository.existsByIsbnAndStatus(isbn, BookStatus.AVAILABLE);
        log.info("ISBN + 상태로 존재 여부 확인 - AVAILABLE : " + result);
    }

    @Test
    @DisplayName("ISBN + 상태로 존재 여부 확인 - AVAILABLE이 아님")
    void existsByIsbnAndStatus_rented() {
        String isbn = "9788937460777";
        boolean result = bookRepository.existsByIsbnAndStatus(isbn, BookStatus.AVAILABLE);
        log.info("ISBN + 상태로 존재 여부 확인 - AVAILABLE이 아님 : " + result);
    }

    @Test
    @DisplayName("ISBN 목록 중 AVAILABLE인 ISBN만 반환")
    void findAvailableIsbnIn() {
        List<String> isbns = List.of("9791136299345", "9788937460777");
        List<String> available = bookRepository.findAvailableIsbnIn(isbns, BookStatus.AVAILABLE);
        log.info("ISBN 목록 중 AVAILABLE인 ISBN : " + available);
    }

    @Test
    @DisplayName("같은 ISBN 여러 권 중 하나라도 AVAILABLE이면 조회")
    void findAvailableIsbnIn_multipleBooks() {
        List<String> available = bookRepository.findAvailableIsbnIn(
                List.of("9791136299345", "9788937460777"), BookStatus.AVAILABLE);
        log.info("같은 ISBN 여러 권 중 하나라도 AVAILABLE이면 조회 : " + available);
    }
}