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

/*
 * ========== RentalResponseDTO 설명 ==========
 * - 역할: 대출 정보를 프론트에 응답할 때 사용하는 DTO
 * - 쓰이는 곳: RentalService.getUserRentals()에서 Rental → RentalResponseDTO 변환 후 RentalController에서 반환
 *
 * [주요 필드]
 * - rentId: 대출 PK
 * - memberId / bookId: 회원 ID / 도서 ID
 * - rentalDate: 대출일
 * - dueDate: 반납 예정일
 * - returnDate: 실제 반납일
 * - status: 대출 상태 (RENTED / RETURNED)
 *
 * [메서드]
 * - from(Rental): Rental 엔티티를 RentalResponseDTO로 변환하는 정적 팩토리 메서드
 */
