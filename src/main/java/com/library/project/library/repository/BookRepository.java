package com.library.project.library.repository;

import com.library.project.library.entity.Book;
import com.library.project.library.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
