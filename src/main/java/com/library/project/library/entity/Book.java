package com.library.project.library.entity;

import com.library.project.library.enums.BookStatus;
import com.library.project.library.enums.RentalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"rentals", "recommends"})
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 책 고유 식별자 (네이버 API 제공)
// 같은 isbn = 같은 책의 여러 권 → isbn으로 묶어서 처리
// 예) isbn=978... 인 row가 3개면 그 책이 3권 있다는 뜻
    @Column(length = 50)
    private String isbn;

    @Column(length = 500, nullable = false)
    private String bookTitle;

    @Column(length = 600)
    private String bookImage;

    @Column(length = 100, nullable = false)
    private String author;

    @Column(length = 500, nullable = false)
    private String publisher;

    private LocalDate pubdate;

    @Column(length = 3000)
    private String description;

    @Column(length = 500)
    private String bookTitleNormal;

    @Column(length = 500)
    private String bookTitleChosung;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    // 📌 연관관계
    @OneToMany(mappedBy = "book")
    @Builder.Default
    private List<Rental> rentals = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    @Builder.Default
    private List<BookRequest> bookRequests = new ArrayList<>();

    public void addRental(Rental rental) {
        this.rentals.add(rental);
        rental.setBook(this);  // 반대쪽도 동기화
    }

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Recommend> recommends = new ArrayList<>();

    // 대여/반납 처리 시 status 변경용 메서드
// 대여 시: AVAILABLE → RENTED
// 반납 시: RENTED → AVAILABLE
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @PrePersist
    private void prePersist() {
// 저자 없으면 기본값 세팅
        if (this.author == null || this.author.trim().isEmpty()) {
            this.author = "작자 미상";
        }
// status가 null이면 기본값 AVAILABLE로 세팅
        if (this.status == null) {
            this.status = BookStatus.AVAILABLE;
        }
    }

    public void rent(){
        this.status = BookStatus.RENTED;
    }


}

/*
 * ========== Book 엔티티 설명 ==========
 * - 역할: 도서관의 개별 도서 한 권을 나타내는 엔티티 (같은 isbn이면 같은 책의 다른 권)
 * - 쓰이는 곳: BookRepository, BookServiceImpl, RentalService에서 사용
 *
 * [주요 필드]
 * - id: DB 자동생성 PK
 * - isbn: 책 고유 식별자 (네이버 API 제공). 같은 isbn = 같은 책의 여러 권
 * - bookTitle / author / publisher / pubdate / description: 도서 기본 정보
 * - bookImage: 도서 표지 이미지 URL
 * - bookTitleNormal: 자모 분리 정규화된 제목 (한글 검색용)
 * - bookTitleChosung: 초성만 추출한 제목 (초성 검색용)
 * - status: 대여 상태 (AVAILABLE / RENTED)
 * - rentals: 이 책의 대여 이력 목록 (OneToMany)
 * - recommends: 이 책의 추천 기록 목록 (OneToMany, 양방향) - 책 삭제 시 추천 기록도 자동 삭제 (cascade + orphanRemoval)
 *
 * [메서드]
 * - setStatus(): 대여/반납 시 status 변경 (BookController, RentalService에서 호출)
 * - prePersist(): DB 저장 전 기본값 세팅 (저자 미입력 시 "작자 미상", status 기본 AVAILABLE)
 * - rent(): 대여 처리 시 status를 RENTED로 변경 (RentalService.rentBook()에서 호출)
 */

