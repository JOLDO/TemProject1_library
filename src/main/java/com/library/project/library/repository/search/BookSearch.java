package com.library.project.library.repository.search;

import com.library.project.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookSearch {
//    boolean existsByBooks();
    Page<Book> searchDistinctAll(String keyword, String keywordNor, String keywordCho, String sort, Pageable pageable);
}
