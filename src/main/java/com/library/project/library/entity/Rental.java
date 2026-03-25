package com.library.project.library.entity;

import com.library.project.library.enums.RentalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rental")
public class Rental extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private int renewCount;

    private LocalDate rentalDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = RentalStatus.RETURNED;
    }

    // 재대출
    public void increaseRenewCount(){
        this.renewCount++;
    }
}

/*
 * ========== Rental 엔티티 설명 ==========
 * - 역할: 도서 대출/반납 기록을 저장하는 엔티티
 * - 쓰이는 곳: RentalRepository, RentalService에서 사용
 *
 * [주요 필드]
 * - id: 대출 고유 번호 (PK)
 * - member: 대출한 회원 (ManyToOne, 지연 로딩)
 * - book: 대출한 도서 (ManyToOne, 지연 로딩)
 * - renewCount: 재대출 횟수 (최대 1회)
 * - rentalDate: 대출 일자
 * - dueDate: 반납 예정일 (대출일 + 14일)
 * - returnDate: 실제 반납일
 * - status: 대출 상태 (RENTED / RETURNED)
 *
 * [메서드]
 * - returnBook(): 반납 처리 (반납일 기록 + status를 RETURNED로 변경). RentalService.returnBook()에서 호출
 * - increaseRenewCount(): 재대출 횟수 증가. RentalService.renewBook()에서 호출
 */
