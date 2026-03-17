package com.library.project.library.service;

import com.library.project.library.dto.rentalDto.RentalRequestDTO;
import com.library.project.library.dto.rentalDto.RentalResponseDTO;
import com.library.project.library.dto.rentalDto.ReturnRequestDTO;
import com.library.project.library.entity.Book;
import com.library.project.library.entity.Rental;
import com.library.project.library.entity.User;
import com.library.project.library.enums.BookStatus;
import com.library.project.library.enums.RentalStatus;
import com.library.project.library.repository.BookRepository;
import com.library.project.library.repository.RentalRepository;
import com.library.project.library.repository.UserRepository;
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
public class RentalServiceTests {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentalRepository rentalRepository;

    /**
     * 📌 도서 대출 테스트
     */
    @Test
    public void testRentBook() {

        // given
        User user = userRepository.save(
                User.builder()
                        .name("테스트유저")
                        .build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .title("자바의 정석")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        RentalRequestDTO dto = RentalRequestDTO.builder()
                .userId(user.getUserId())
                .bookId(book.getBookId())
                .build();

        // when
        rentalService.rentBook(dto);

        // then
        log.info("대출 완료 확인");
    }

    /**
     * 📌 도서 반납 테스트
     */
    @Test
    public void testReturnBook() {

        // given
        User user = userRepository.save(
                User.builder().name("유저1").build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .title("스프링 부트")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        Rental rental = rentalRepository.save(
                Rental.builder()
                        .user(user)
                        .book(book)
                        .status(RentalStatus.RENTED)
                        .rentalDate(LocalDate.now())
                        .build()
        );

        ReturnRequestDTO dto = new ReturnRequestDTO(rental.getId());

        // when
        rentalService.returnBook(dto);

        // then
        Rental result = rentalRepository.findById(rental.getId()).get();
        log.info("반납 상태 확인 : " + result.getStatus());
    }

    /**
     * 📌 사용자 대출 목록 조회 테스트
     */
    @Test
    public void testGetUserRentals() {

        // given
        User user = userRepository.save(
                User.builder().name("조회유저").build()
        );

        Book book = bookRepository.save(
                Book.builder()
                        .title("JPA 책")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        rentalRepository.save(
                Rental.builder()
                        .user(user)
                        .book(book)
                        .status(RentalStatus.RENTED)
                        .rentalDate(LocalDate.now())
                        .build()
        );

        // when
        List<RentalResponseDTO> list =
                rentalService.getUserRentals(user.getUserId());

        // then
        log.info("대출 목록 개수 : " + list.size());
        list.forEach(r -> log.info(r));
    }

    /**
     * 📌 인기 도서 통계 테스트
     */
    @Test
    public void testMostRentedBooks() {

        // given
        User user = userRepository.save(
                User.builder().name("통계유저").build()
        );

        Book book1 = bookRepository.save(
                Book.builder()
                        .title("인기책1")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        Book book2 = bookRepository.save(
                Book.builder()
                        .title("인기책2")
                        .status(BookStatus.AVAILABLE)
                        .build()
        );

        // book1 → 2번 대출
        rentalRepository.save(Rental.builder().user(user).book(book1).status(RentalStatus.RENTED).build());
        rentalRepository.save(Rental.builder().user(user).book(book1).status(RentalStatus.RETURNED).build());

        // book2 → 1번 대출
        rentalRepository.save(Rental.builder().user(user).book(book2).status(RentalStatus.RENTED).build());

        // when
        List<Object[]> result = rentalService.getMostRentedBooks();

        // then
        result.forEach(arr -> {
            log.info("bookId : " + arr[0] + ", count : " + arr[1]);
        });
    }

}
