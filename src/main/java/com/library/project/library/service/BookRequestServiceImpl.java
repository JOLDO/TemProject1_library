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

    // ─────────────────────────────────────────────────────────────────
    // 대출 신청 (회원)
    //
    // [전체 흐름]
    // 1. 프론트에서 넘어오는 bookId는 isbn 대표 row(min id)
    //    → 해당 bookId로 isbn을 알아낸 뒤, 같은 isbn의 모든 권을 대상으로 처리
    // 2. isbn + memberId 기준으로 중복 PENDING 신청 체크
    // 3. 같은 isbn의 book_id별 PENDING 예약 수를 카운트
    //    → 예약이 가장 적은 book_id에 배정 (균등 분산)
    //    → 대여 가능한 권이 없어도 예약 가능
    // 4. BookRequest 생성 후 저장
    //
    // [예시] 나루토 id 1(예약 2건), id 2(예약 0건) → id 2에 예약 배정
    // ─────────────────────────────────────────────────────────────────
    /** 대출 신청 (회원) */
    @Override
    public void requestBook(BookRequestDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 프론트에서 넘어오는 bookId는 isbn 대표 row(min id)이므로
        // 해당 isbn을 가져온 뒤, 균등 분산 로직으로 실제 권을 결정
        Book representativeBook = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("도서 없음"));
        String isbn = representativeBook.getIsbn();

        // 이미 같은 isbn에 PENDING 신청이 있으면 중복 신청 방지 (isbn 기준 체크)
        if (bookRequestRepository.existsByBook_IsbnAndMember_IdAndStatus(isbn, dto.getMemberId(), RequestStatus.PENDING)) {
            throw new RuntimeException("이미 신청 중인 도서입니다.");
        }

        // 같은 isbn의 book_id별 PENDING 예약 수를 조회해서 가장 적은 권에 배정
        // countPendingPerBookByIsbn(): count 오름차순 → get(0)이 예약 가장 적은 권
        // → 예약이 균등하게 분산됨 (대여 가능한 권이 없어도 예약 가능)
        List<Object[]> counts = bookRequestRepository.countPendingPerBookByIsbn(isbn, RequestStatus.PENDING);
        if (counts.isEmpty()) {
            throw new RuntimeException("해당 도서를 찾을 수 없습니다.");
        }
        Long targetBookId = (Long) counts.get(0)[0]; // 예약 가장 적은 book_id
        Book targetBook = bookRepository.findById(targetBookId)
                .orElseThrow(() -> new RuntimeException("도서 없음"));

        BookRequest request = BookRequest.builder()
                .member(member)
                .book(targetBook)
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

    // ─────────────────────────────────────────────────────────────────
    // 신청 승인 → 실제 대출 처리 (관리자)
    //
    // [핵심] 예약 시점의 book_id가 아니라, 승인 시점에 같은 isbn 중
    //        AVAILABLE인 권을 다시 찾아서 대출 처리
    //        → 예약은 id 2에 걸려있었어도 승인 시 id 1이 반납됐으면 id 1로 대출
    //        → AVAILABLE인 권이 없으면 승인 불가 (반납 후 재시도)
    // ─────────────────────────────────────────────────────────────────
    /** 신청 승인 → 실제 대출 처리 (관리자) */
    @Override
    public void approveRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("신청 정보 없음"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("이미 처리된 신청입니다.");
        }

        // 승인 시점에 같은 isbn 중 AVAILABLE인 권을 찾아서 대출
        // 예약 시점의 book_id가 RENTED 상태여도, 다른 권이 AVAILABLE이면 그걸로 대출
        String isbn = request.getBook().getIsbn();
        Book availableBook = bookRepository.findFirstByIsbnAndStatus(isbn, BookStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("대여 가능한 권이 없습니다. 반납 후 다시 시도해주세요."));

        // 상태 변경
        request.setStatus(RequestStatus.APPROVED);

        // 실제 대출 처리 (AVAILABLE인 권으로)
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
