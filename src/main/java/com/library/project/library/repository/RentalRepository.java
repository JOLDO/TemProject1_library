package com.library.project.library.repository;


import com.library.project.library.entity.Rental;
import com.library.project.library.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByMember_IdAndStatus(Long MemberId, RentalStatus status);

    Optional<Rental> findByBook_IdAndStatus(Long bookId, RentalStatus status);

        @Query("""
        SELECT r.book.id, COUNT(r)
        FROM Rental r
        GROUP BY r.book.id
        ORDER BY COUNT(r) DESC
    """)
        List<Object[]> findMostRentedBooks();

    // 오늘 대출 개수
    @Query("""
        SELECT COUNT(r)
        FROM Rental r
        WHERE r.member.id = :memberId
        AND r.rentalDate = :today
    """)
    int countTodayRentals(Long memberId, LocalDate today);


}
