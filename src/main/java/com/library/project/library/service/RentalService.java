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
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    // 도서 대출
    public void rentBook(RentalRequestDTO dto){

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new RuntimeException("책 없음"));

        // 이미 대출중인지 확인
        rentalRepository.findByBook_BookIdAndStatus(dto.getBookId(), RentalStatus.RENTED)
                .ifPresent(r -> {
                    throw new RuntimeException("이미 대출된 책입니다.");
                });

        Rental rental = Rental.builder()
                .user(user)
                .book(book)
                .rentalDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(RentalStatus.RENTED)
                .build();

        rentalRepository.save(rental);
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
    public List<RentalResponseDTO> getUserRentals(Long userId){

        List<Rental> rentals =
                rentalRepository.findByUser_UserIdAndStatus(userId, RentalStatus.RENTED);

        return rentals.stream()
                .map(RentalResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMostRentedBooks() {

        return rentalRepository.findMostRentedBooks();
    }
}
