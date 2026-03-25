package com.library.project.library.dto.rentalDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnRequestDTO {

    private Long rentalId;

}

/*
 * ========== ReturnRequestDTO 설명 ==========
 * - 역할: 도서 반납 요청 시 프론트에서 보내는 데이터를 담는 DTO
 * - 쓰이는 곳: RentalController.returnBook()에서 @RequestBody로 수신 → RentalService.returnBook()에 전달
 *
 * [주요 필드]
 * - rentalId: 반납할 대출 기록의 ID
 */
