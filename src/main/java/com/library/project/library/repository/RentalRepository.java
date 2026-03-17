package com.library.project.library.repository;


import com.library.project.library.entity.Rental;
import com.library.project.library.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUser_UserIdAndStatus(Long userId, RentalStatus status);

    Optional<Rental> findByBook_BookIdAndStatus(Long bookId, RentalStatus status);

        @Query("""
        SELECT r.book.bookId, COUNT(r)
        FROM Rental r
        GROUP BY r.book.bookId
        ORDER BY COUNT(r) DESC
    """)
        List<Object[]> findMostRentedBooks();


}
