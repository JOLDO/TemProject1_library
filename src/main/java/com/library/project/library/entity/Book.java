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
@ToString
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
