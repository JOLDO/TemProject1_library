package com.library.project.library.dto.rentalDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalRequestDTO {

    private Long memberId;
    private Long bookId;

}

/*
 * ========== RentalRequestDTO 설명 ==========
 * - 역할: 도서 대출 요청 시 프론트에서 보내는 데이터를 담는 DTO
 * - 쓰이는 곳: RentalController.rentBook()에서 @RequestBody로 수신 → RentalService.rentBook()에 전달
 *
 * [주요 필드]
 * - memberId: 대출 요청한 회원 ID
 * - bookId: 대출할 도서 ID
 */

