package com.library.project.library.service;

import com.library.project.library.dto.BookRequestDto.BookRequestDTO;
import com.library.project.library.dto.BookRequestDto.BookRequestResponseDTO;
import com.library.project.library.entity.Book;
import com.library.project.library.entity.BookRequest;
import com.library.project.library.entity.Member;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.enums.RequestStatus;
import com.library.project.library.repository.BookRepository;
import com.library.project.library.repository.BookRequestRepository;
import com.library.project.library.repository.MemberRepository;
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
@Transactional
class BookRequestServiceTest {

    @Autowired BookRequestService bookRequestService;
    @Autowired BookRequestRepository bookRequestRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired BookRepository bookRepository;

    private Member testMember;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("테스트 회원 없음"));

        testBook = Book.builder()
                .bookTitle("테스트 도서")
                .author("테스트 저자")
                .isbn("1234567890")
                .publisher("테스트 출판사")   // ← 추가
                .pubdate(LocalDate.of(2024, 1, 1))          // ← 추가 (형식 확인 필요)
                .status(BookStatus.AVAILABLE)
                .build();
        bookRepository.save(testBook);
    }

    @Test
    @DisplayName("대출 신청 성공")
    void requestBook_success() {
        // given
        BookRequestDTO dto = new BookRequestDTO();
        // memberId, bookId setter 방식으로 설정
        // (DTO에 @Setter 또는 @Data 필요)

        // when
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );

        // then
        List<BookRequestResponseDTO> list = bookRequestService.getPendingRequests();
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("중복 신청 방지")
    void requestBook_duplicate() {
        // given - 첫 번째 신청
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );

        // when & then - 두 번째 신청은 예외 발생
        assertThatThrownBy(() ->
                bookRequestService.requestBook(
                        BookRequestDTO.builder()
                                .memberId(testMember.getId())
                                .bookId(testBook.getId())
                                .build()
                )
        ).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 신청 중인 도서입니다.");
    }

    @Test
    @DisplayName("신청 승인 → 대출 처리")
    void approveRequest_success() {
        // given - 신청 먼저
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );

        List<BookRequestResponseDTO> pending = bookRequestService.getPendingRequests();
        Long requestId = pending.get(0).getRequestId();

        // when - 승인
        bookRequestService.approveRequest(requestId);

        // then - 상태가 APPROVED로 변경
        BookRequest request = bookRequestRepository.findById(requestId).orElseThrow();
        assertThat(request.getStatus()).isEqualTo(RequestStatus.APPROVED);
    }

    @Test
    @DisplayName("신청 거절")
    void rejectRequest_success() {
        // given
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );

        List<BookRequestResponseDTO> pending = bookRequestService.getPendingRequests();
        Long requestId = pending.get(0).getRequestId();

        // when
        bookRequestService.rejectRequest(requestId);

        // then
        BookRequest request = bookRequestRepository.findById(requestId).orElseThrow();
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    @DisplayName("이미 처리된 신청 재승인 시 예외")
    void approveRequest_alreadyProcessed() {
        // given
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );

        List<BookRequestResponseDTO> pending = bookRequestService.getPendingRequests();
        Long requestId = pending.get(0).getRequestId();
        bookRequestService.approveRequest(requestId); // 한 번 승인

        // when & then - 다시 승인하면 예외
        assertThatThrownBy(() -> bookRequestService.approveRequest(requestId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 처리된 신청입니다.");
    }

    @Test
    @DisplayName("회원별 신청 목록 조회")
    void getMyRequests_success() {
        // given
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );

        // when
        List<BookRequestResponseDTO> list =
                bookRequestService.getMyRequests(testMember.getId());

        // then
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getMemberId()).isEqualTo(testMember.getId());
    }

    @Test
    @DisplayName("대출 신청 취소 (ISBN 기준)")
    void cancelRequest_success() {
        // given - 신청
        bookRequestService.requestBook(
                BookRequestDTO.builder()
                        .memberId(testMember.getId())
                        .bookId(testBook.getId())
                        .build()
        );
        bookRequestService.cancelRequest(testMember.getId(), testBook.getIsbn());
    }
}