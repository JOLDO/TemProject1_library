package com.library.project.library.enums;

public enum BookStatus {

    AVAILABLE,
    RENTED
}

/*
 * ========== BookStatus 설명 ==========
 * - 역할: 도서의 대여 상태를 나타내는 열거형
 * - 쓰이는 곳: Book 엔티티의 status 필드, BookServiceImpl, RentalService
 *
 * [값]
 * - AVAILABLE: 대여 가능 (서가에 비치된 상태)
 * - RENTED: 대여 중 (누군가 빌려간 상태)
 */
