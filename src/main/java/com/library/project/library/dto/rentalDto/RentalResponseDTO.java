package com.library.project.library.dto.rentalDto;

import com.library.project.library.entity.Rental;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RentalResponseDTO {

    private Long rentalId;
    private Long memberId;
    private Long bookId;
    private String bookTitle;   // ← 추가
    private String bookAuthor;  // ← 추가
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private int renewCount;     // ← 추가

    public static RentalResponseDTO from(Rental rental) {
        return RentalResponseDTO.builder()
                .rentalId(rental.getId())
                .memberId(rental.getMember().getId())
                .bookId(rental.getBook().getId())
                .bookTitle(rental.getBook().getBookTitle())  // ← 추가
                .bookAuthor(rental.getBook().getAuthor())    // ← 추가
                .rentalDate(rental.getRentalDate())
                .dueDate(rental.getDueDate())
                .returnDate(rental.getReturnDate())
                .status(rental.getStatus().name())
                .renewCount(rental.getRenewCount())          // ← 추가
                .build();
    }
}
