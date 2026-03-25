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
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@Log4j2
@Transactional
class RentalServiceTests {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * ✅ 1. 대출 성공 + 상태 검증
     */
    @Test
    void testRentBook_success() {

        Member member = memberRepository.save(
                Member.builder()
                        .mid("user1")
                        .mpw("1234")
                        .mname("테스트유저")
                        .build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .bookTitle("자바의 정석")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        RentalRequestDTO dto = RentalRequestDTO.builder()
                .memberId(member.getId())
                .bookId(book.getId())
                .build();

        rentalService.rentBook(dto);

        // ⭐ 검증
        List<Rental> rentals = rentalRepository.findAll();

        assert rentals.size() == 1;
        assert rentals.get(0).getStatus() == RentalStatus.RENTED;

        Book resultBook = bookRepository.findById(book.getId()).get();
        assert resultBook.getStatus() == BookStatus.RENTED;

        log.info("대출 성공 검증 완료");
    }

    /**
     * ❌ 2. 하루 3권 제한 테스트
     */
    @Test
    void testRentBook_overLimit() {

        Member member = memberRepository.save(
                Member.builder()
                        .mid("user2")
                        .mpw("1234")
                        .mname("제한유저")
                        .build()
        );

        // 책 3개
        for(int i = 0; i < 3; i++){
            Book book = bookRepository.save(
                    Book.builder()
                            .bookTitle("책" + i)
                            .status(BookStatus.AVAILABLE)
                            .build()
            );

            rentalRepository.save(
                    Rental.builder()
                            .member(member)
                            .book(book)
                            .status(RentalStatus.RENTED)
                            .rentalDate(LocalDate.now())
                            .build()
            );
        }

        Book newBook = bookRepository.save(
                Book.builder()
                        .bookTitle("초과책")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        RentalRequestDTO dto = RentalRequestDTO.builder()
                .memberId(member.getId())
                .bookId(newBook.getId())
                .build();

        try {
            rentalService.rentBook(dto);
            assert false; // 실패해야 정상
        } catch (RuntimeException e){
            assert e.getMessage().contains("하루 최대 3권");
        }

        log.info("대출 제한 테스트 완료");
    }

    /**
     * ❌ 3. 이미 대출된 책
     */
    @Test
    void testRentBook_alreadyRented() {

        Member member = memberRepository.save(
                Member.builder()
                        .mid("user3")
                        .mpw("1234")
                        .mname("중복유저")
                        .build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .bookTitle("중복책")
                        .status(BookStatus.RENTED)
                        .build()
        );

        rentalRepository.save(
                Rental.builder()
                        .member(member)
                        .book(book)
                        .status(RentalStatus.RENTED)
                        .rentalDate(LocalDate.now())
                        .build()
        );

        RentalRequestDTO dto = RentalRequestDTO.builder()
                .memberId(member.getId())
                .bookId(book.getId())
                .build();

        try {
            rentalService.rentBook(dto);
            assert false;
        } catch (RuntimeException e){
            assert e.getMessage().contains("이미 대출된");
        }
    }

    /**
     * ✅ 4. 반납 성공 + 상태 검증
     */
    @Test
    void testReturnBook_success() {

        Member member = memberRepository.save(
                Member.builder()
                        .mid("user4")
                        .mpw("1234")
                        .mname("반납유저")
                        .build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .bookTitle("반납책")
                        .status(BookStatus.RENTED)
                        .build()
        );

        Rental rental = rentalRepository.save(
                Rental.builder()
                        .member(member)
                        .book(book)
                        .status(RentalStatus.RENTED)
                        .rentalDate(LocalDate.now())
                        .build()
        );

        rentalService.returnBook(new ReturnRequestDTO(rental.getId()));

        Rental result = rentalRepository.findById(rental.getId()).get();

        assert result.getStatus() == RentalStatus.RETURNED;
        assert result.getReturnDate() != null;

        Book resultBook = bookRepository.findById(book.getId()).get();
        assert resultBook.getStatus() == BookStatus.AVAILABLE;

        log.info("반납 성공 검증 완료");
    }

    /**
     * ❌ 5. 이미 반납된 경우
     */
    @Test
    void testReturnBook_alreadyReturned() {

        Member member = memberRepository.save(
                Member.builder()
                        .mid("user5")
                        .mpw("1234")
                        .mname("예외유저")
                        .build()
        );

        Rental rental = rentalRepository.save(
                Rental.builder()
                        .member(member)
                        .status(RentalStatus.RETURNED)
                        .build()
        );

        try {
            rentalService.returnBook(new ReturnRequestDTO(rental.getId()));
            assert false;
        } catch (RuntimeException e){
            assert e.getMessage().contains("이미 반납");
        }
    }

    /**
     * ❌ 6. 재대출 제한
     */
    @Test
    void testRenew_overLimit() {

        Rental rental = rentalRepository.save(
                Rental.builder()
                        .renewCount(1)
                        .status(RentalStatus.RENTED)
                        .build()
        );

        try {
            rentalService.renewBook(rental.getId());
            assert false;
        } catch (RuntimeException e){
            assert e.getMessage().contains("재대출");
        }
    }

    /**
     * ✅ 7. 조회 테스트
     */
    @Test
    void testGetUserRentals() {

        Member member = memberRepository.save(
                Member.builder()
                        .mid("user6")
                        .mpw("1234")
                        .mname("조회유저")
                        .build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .bookTitle("조회책")
                        .status(BookStatus.RENTED)
                        .build()
        );

        rentalRepository.save(
                Rental.builder()
                        .member(member)
                        .book(book)
                        .status(RentalStatus.RENTED)
                        .build()
        );

        List<RentalResponseDTO> list =
                rentalService.getUserRentals(member.getId());

        assert list.size() == 1;
        assert list.get(0).getMemberId().equals(member.getId());

        log.info("조회 테스트 완료");
    }
}