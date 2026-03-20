package com.library.project.library.repository.search;

import com.library.project.library.entity.Book;
import com.library.project.library.entity.QBook;
import com.library.project.library.entity.QRecommend;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class BookSearchImpl extends QuerydslRepositorySupport implements BookSearch {
    public BookSearchImpl() {
        super(Book.class);
    }

    // Book 데이터가 하나라도 있는지 확인 (앱 시작 시 API 중복 호출 방지용)
    /*@Override
    public boolean existsByBooks() {
        QBook book = QBook.book;
        JPQLQuery<Book> query = from(book);
        return query.fetchCount() > 0;
    }*/

    // isbn 기준으로 중복 제거된 책 목록 조회 (검색 + 페이징 포함)
    //
    // [핵심 설계]
    // Book 테이블에는 같은 책이 여러 row로 저장됨 (권 수만큼)
    // 예) isbn=978... 인 row가 3개 → 그 책이 3권 있다는 뜻
    // 리스트 화면에서는 isbn당 1개만 보여줘야 하므로
    // 서브쿼리로 isbn별 MIN(id)를 구해서 대표 row 하나씩만 가져옴
    @Override
    public Page<Book> searchDistinctAll(String keyword, String keywordNor, String keywordCho, String sort, Pageable pageable) {
        QBook book = QBook.book;
        QRecommend recommend = QRecommend.recommend;
        // QRentalHistory rental = QRentalHistory.rentalHistory;
        JPQLQuery<Book> query = from(book);

        // isbn별 가장 먼저 저장된 row의 id(min)를 서브쿼리로 추출
        // → 같은 isbn 중 대표 row 하나씩만 메인 쿼리에서 조회
        //JPAExpressions는 서브 쿼리 만들때 씀
        JPQLQuery<Long> minIdPerIsbn = JPAExpressions
                .select(book.id.min())
                .from(book)
                .groupBy(book.isbn);
        query.where(book.id.in(minIdPerIsbn));

        //쿼리로 키워드로 검색
        if (keyword != null && !keyword.isBlank()) {
            query.where(book.bookTitle.contains(keyword)
                    .or(book.bookTitleNormal.contains(keywordNor))
                    .or(book.bookTitleChosung.contains(keywordCho))
                    .or(book.author.contains(keyword))
                    .or(book.publisher.contains(keyword))
                    .or(book.description.contains(keyword)));

        }
        // 정렬 적용 전 total 계산 (join 전)
        long total = query.fetchCount();

        //검색한 데이터에서 우선순위 정해서 정렬을 함
        NumberExpression<Integer> priority = keyword != null && !keyword.isBlank() ?
                new CaseBuilder()
                    .when(book.bookTitle.contains(keyword)).then(1)
                    .when(book.bookTitleNormal.contains(keywordNor)).then(2)
                    .when(book.bookTitleChosung.contains(keywordCho)).then(3)
                    .when(book.author.contains(keyword)).then(4)
                    .when(book.publisher.contains(keyword)).then(5)
                    .when(book.description.contains(keyword)).then(6)
                    .otherwise(7) : null;

        // 정렬 기준이 있으면 1순위로 정렬 하고 나서 2순위로 우선수 정렬 함
        switch (sort != null ? sort : "id") {
            case "recommend":
                // ===============================
                // 추천 수 기준 내림차순 정렬
                query.leftJoin(recommend).on(recommend.book.id.eq(book.id))
                        .groupBy(book.id)
                        .orderBy(recommend.count().desc());
                if(priority != null)    query.orderBy(priority.asc());
                // ===============================
                break;
            case "rental":
                // ===============================
                // 렌탈 수 기준 내림차순 정렬
                /*query.leftJoin(rental).on(rental.book.id.eq(book.id))
                        .groupBy(book.id)
                        .orderBy(rental.count().desc());
                if(priority != null)    query.orderBy(priority.asc());*/
                // ===============================
                break;
            case "pubdate":
                query.orderBy(book.pubdate.desc());
                if(priority != null)    query.orderBy(priority.asc());
                break;
            case "bookTitle":
                query.orderBy(book.bookTitle.asc());
                if(priority != null)    query.orderBy(priority.asc());
                break;
            case "regDate":
                query.orderBy(book.regDate.desc());
                if(priority != null)    query.orderBy(priority.asc());
                break;
            default:
                if(priority != null)    query.orderBy(priority.asc());
                else    query.orderBy(book.id.desc());
                break;
        }
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        List<Book> list = query.fetch();

        return new PageImpl<>(list, pageable, total);
    }
}