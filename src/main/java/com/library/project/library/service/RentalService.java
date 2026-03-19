package com.library.project.library.service;

import com.library.project.library.dto.rentalDto.RentalRequestDTO;
import com.library.project.library.dto.rentalDto.RentalResponseDTO;
import com.library.project.library.dto.rentalDto.ReturnRequestDTO;
import com.library.project.library.entity.Book;
import com.library.project.library.entity.Member;
import com.library.project.library.entity.Rental;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.enums.RentalStatus;
import com.library.project.library.repository.BookRepository;
import com.library.project.library.repository.RentalRepository;
import com.library.project.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    /**
     * 📌 도서 대출
     */
    public void rentBook(RentalRequestDTO dto){

        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("책 없음"));

        // ⭐ 하루 3권 제한
        int todayCount = rentalRepository.countTodayRentals(
                member.getId(),
                LocalDate.now()
        );

        if(todayCount >= 3){
            throw new RuntimeException("하루 최대 3권까지 대출 가능합니다.");
        }

        // ⭐ 이미 대출된 책인지
        rentalRepository.findByBook_IdAndStatus(
                book.getId(),
                RentalStatus.RENTED
        ).ifPresent(r -> {
            throw new RuntimeException("이미 대출된 책입니다.");
        });

        Rental rental = Rental.builder()
                .member(member)
                .book(book)
                .rentalDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(RentalStatus.RENTED)
                .build();

        rentalRepository.save(rental);

        book.rent();
    }

    /**
     * 📌 재대출
     */
    public void renewBook(Long rentalId){

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("대출 없음"));

        if(rental.getRenewCount() >= 1){
            throw new RuntimeException("재대출은 1회만 가능합니다.");
        }

        rental.increaseRenewCount();
        rental.returnBook(); // 기존 종료

    }

    // 도서 반납
    public void returnBook(ReturnRequestDTO dto){

        Rental rental = rentalRepository.findById(dto.getRentalId())
                .orElseThrow(() -> new RuntimeException("대출 정보 없음"));

        if(rental.getStatus() == RentalStatus.RETURNED){
            throw new RuntimeException("이미 반납된 도서입니다.");
        }

        rental.setReturnDate(LocalDate.now());
        rental.setStatus(RentalStatus.RETURNED);

        Book book = rental.getBook();
        book.setStatus(BookStatus.AVAILABLE);
    }


    // 사용자 대출 목록 조회
    @Transactional(readOnly = true)
    public List<RentalResponseDTO> getUserRentals(Long MemberId){

        List<Rental> rentals =
                rentalRepository.findByMember_IdAndStatus(MemberId, RentalStatus.RENTED);

        return rentals.stream()
                .map(RentalResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMostRentedBooks() {

        return rentalRepository.findMostRentedBooks();
    }
}
