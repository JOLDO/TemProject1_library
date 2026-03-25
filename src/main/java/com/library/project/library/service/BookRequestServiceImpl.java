package com.library.project.library.service;

import com.library.project.library.dto.BookRequestDto.BookRequestDTO;
import com.library.project.library.dto.BookRequestDto.BookRequestResponseDTO;
import com.library.project.library.dto.rentalDto.RentalRequestDTO;
import com.library.project.library.entity.Book;
import com.library.project.library.entity.BookRequest;
import com.library.project.library.entity.Member;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.enums.RequestStatus;
import com.library.project.library.repository.BookRepository;
import com.library.project.library.repository.BookRequestRepository;
import com.library.project.library.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookRequestServiceImpl implements BookRequestService {

    private final BookRequestRepository bookRequestRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final RentalService rentalService; // 승인 시 대출 처리

    /** 대출 신청 (회원) */
    @Override
    public void requestBook(BookRequestDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("도서 없음"));

        // 이미 PENDING 신청이 있으면 중복 신청 방지
        if (bookRequestRepository.existsByMember_IdAndBook_IdAndStatus(member.getId(), dto.getBookId(), RequestStatus.PENDING)) {
            throw new RuntimeException("이미 신청 중인 도서입니다.");
        }

        BookRequest request = BookRequest.builder()
                .member(member)
                .book(book)
                .requestDate(LocalDate.now())
                .status(RequestStatus.PENDING)
                .build();

        bookRequestRepository.save(request);
    }

    /** 신청 목록 조회 - PENDING만 (관리자) */
    @Override
    @Transactional(readOnly = true)
    public List<BookRequestResponseDTO> getPendingRequests() {
        return bookRequestRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(BookRequestResponseDTO::from)
                .toList();
    }

    /** 신청 승인 → 실제 대출 처리 (관리자) */
    @Override
    public void approveRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("신청 정보 없음"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("이미 처리된 신청입니다.");
        }

        // 예약된 책의 ISBN으로 AVAILABLE인 권을 찾아서 대출 처리
        // → 예약 시 고정된 book_id가 아닌, 승인 시점에 비어있는 권을 배정
        String isbn = request.getBook().getIsbn();
        Book availableBook = bookRepository.findFirstByIsbnAndStatus(isbn, BookStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("대여 가능한 책이 없습니다."));

        // 상태 변경
        request.setStatus(RequestStatus.APPROVED);

        // 실제 대출 처리 (AVAILABLE인 권의 id로 대출)
        RentalRequestDTO rentalDTO = RentalRequestDTO.builder()
                .memberId(request.getMember().getId())
                .bookId(availableBook.getId())
                .build();
        rentalService.rentBook(rentalDTO);
    }

    /** 신청 거절 (관리자) */
    @Override
    public void rejectRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("신청 정보 없음"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("이미 처리된 신청입니다.");
        }

        request.setStatus(RequestStatus.REJECTED);
    }

    /** 회원별 신청 목록 조회 */
    @Override
    @Transactional(readOnly = true)
    public List<BookRequestResponseDTO> getMyRequests(Long memberId) {
        return bookRequestRepository.findByMember_Id(memberId)
                .stream()
                .map(BookRequestResponseDTO::from)
                .toList();
    }

    /** 대출 신청 취소 (PENDING 상태인 예약을 ISBN 기준으로 삭제) */
    @Override
    public void cancelRequest(Long memberId, String isbn) {
        bookRequestRepository.deleteByMemberIdAndIsbnAndStatus(memberId, isbn, RequestStatus.PENDING);
    }
}
