package com.library.project.library.entity;

import com.library.project.library.enums.BookStatus;
import com.library.project.library.enums.RentalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book")
public class Book {

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

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    // 📌 연관관계
    @OneToMany(mappedBy = "book")
    @Builder.Default
    private List<Rental> rentals = new ArrayList<>();

    // 대여/반납 처리 시 status 변경용 메서드
    // 대여 시: AVAILABLE → RENTED
    // 반납 시: RENTED → AVAILABLE
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void rent(){
        this.status = BookStatus.RENTED;
    }
}
