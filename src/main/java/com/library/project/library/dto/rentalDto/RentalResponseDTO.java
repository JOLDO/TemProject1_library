package com.library.project.library.dto.rentalDto;

import com.library.project.library.entity.Rental;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RentalResponseDTO {

    private Long rentId;
    private Long memberId;
    private Long bookId;
    private LocalDate rentalDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

    public static RentalResponseDTO from(Rental rental){
        return RentalResponseDTO.builder()
                .rentId(rental.getId())
                .memberId(rental.getMember().getId())
                .bookId(rental.getBook().getId())
                .rentalDate(rental.getRentalDate())
                .dueDate(rental.getDueDate())
                .returnDate(rental.getReturnDate())
                .status(rental.getStatus().name())
                .build();
    }
}
