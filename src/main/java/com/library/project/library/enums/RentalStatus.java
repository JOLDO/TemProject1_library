package com.library.project.library.enums;

import lombok.Setter;


public enum RentalStatus {

    RENTED,
    RETURNED
}

/*
 * ========== RentalStatus 설명 ==========
 * - 역할: 대출 기록의 상태를 나타내는 열거형
 * - 쓰이는 곳: Rental 엔티티의 status 필드, RentalService, RentalRepository
 *
 * [값]
 * - RENTED: 대출 중 (아직 반납하지 않은 상태)
 * - RETURNED: 반납 완료 (도서를 반환한 상태)
 */
