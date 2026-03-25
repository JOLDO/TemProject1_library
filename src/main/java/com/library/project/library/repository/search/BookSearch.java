package com.library.project.library.repository.search;

import com.library.project.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookSearch {
//    boolean existsByBooks();
    Page<Book> searchDistinctAll(String keyword, String keywordNor, String keywordCho, String sort, Pageable pageable);
}

/*
 * ========== BookSearch 설명 ==========
 * - 역할: QueryDSL 기반 커스텀 검색 인터페이스
 * - 쓰이는 곳: BookRepository가 상속 → BookSearchImpl에서 구현
 *
 * [메서드]
 * - searchDistinctAll(): isbn 중복 제거 + 키워드 검색(일반/정규화/초성) + 정렬 + 페이징 처리
 *   → BookServiceImpl.list()에서 호출
 */
