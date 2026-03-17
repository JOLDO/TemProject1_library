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
    @Column(name = "book_id")
    private Long bookId;

    private String title;

    private String author;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    // 📌 연관관계
    @OneToMany(mappedBy = "book")
    @Builder.Default
    private List<Rental> rentals = new ArrayList<>();


}
