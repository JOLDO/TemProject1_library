package com.library.project.library.repository.search;

import com.library.project.library.entity.Book;
import com.library.project.library.entity.QBook;
import com.library.project.library.entity.QRecommend;
import com.library.project.library.entity.QRental;
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
        QRental rental = QRental.rental;
        JPQLQuery<Book> query = from(book);

        // isbn별 가장 먼저 저장된 row의 id(min)를 서브쿼리로 추출
        // → 같은 isbn 중 대표 row 하나씩만 메인 쿼리에서 조회
        //JPAExpressions는 서브 쿼리 만들때 씀
        JPQLQuery<Long> minIdPerIsbn = JPAExpressions
                .select(book.id.min())
                .from(book)
                .groupBy(book.isbn);
        query.where(book.id.in(minIdPerIsbn));

        // ── 키워드 검색 조건 ──────────────────────────────────────────
        // 제목(원본/정규화) + 저자 + 출판사 + 설명에서 검색
        // 초성 검색(bookTitleChosung)은 keywordCho가 null이 아닐 때만 적용
        // → BookServiceImpl에서 키워드가 순수 초성(ㄱ~ㅎ)일 때만 keywordCho를 전달
        //   "데미안" 같은 일반 단어는 초성 변환 시 "ㄷㅁㅇ"이 되어
        //   "동물의 세계"(초성 "ㄷㅁㅇㅅㄱ")까지 잘못 매칭되는 문제 방지
        if (keyword != null && !keyword.isBlank()) {
            var condition = book.bookTitle.contains(keyword)
                    .or(book.bookTitleNormal.contains(keywordNor))
                    .or(book.author.contains(keyword))
                    .or(book.publisher.contains(keyword))
                    .or(book.description.contains(keyword));
            // 초성 검색은 키워드가 순수 초성일 때만 적용
            if (keywordCho != null) {
                condition = condition.or(book.bookTitleChosung.contains(keywordCho));
            }
            query.where(condition);
        }
        // 정렬 적용 전 total 계산 (join 전)
        long total = query.fetchCount();

        // ── 검색 결과 우선순위 정렬 ──────────────────────────────────
        // 제목 직접 일치(1) > 정규화 일치(2) > 초성 일치(3, 초성 검색 시만)
        // > 저자(4) > 출판사(5) > 설명(6) > 그 외(7)
        // 초성 우선순위(3)는 keywordCho가 null이 아닐 때만 CASE 절에 포함
        NumberExpression<Integer> priority;
        if (keyword != null && !keyword.isBlank()) {
            var cb = new CaseBuilder()
                    .when(book.bookTitle.contains(keyword)).then(1)
                    .when(book.bookTitleNormal.contains(keywordNor)).then(2);
            // 초성 우선순위는 초성 검색이 활성화됐을 때만 적용
            if (keywordCho != null) {
                cb = cb.when(book.bookTitleChosung.contains(keywordCho)).then(3);
            }
            priority = cb
                    .when(book.author.contains(keyword)).then(4)
                    .when(book.publisher.contains(keyword)).then(5)
                    .when(book.description.contains(keyword)).then(6)
                    .otherwise(7);
        } else {
            priority = null;
        }

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
                query.leftJoin(rental).on(rental.book.id.eq(book.id))
                        .groupBy(book.id)
                        .orderBy(rental.count().desc());
                if(priority != null)    query.orderBy(priority.asc());
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

/*
 * ========== BookSearchImpl 설명 ==========
 * - 역할: BookSearch 인터페이스의 QueryDSL 구현체
 * - 쓰이는 곳: BookRepository를 통해 BookServiceImpl.list()에서 간접 호출
 *
 * [searchDistinctAll() 동작 흐름]
 * 1. isbn별 MIN(id) 서브쿼리로 대표 row만 필터링 (같은 책 여러 권 중 하나만 표시)
 * 2. 키워드 검색: 제목(원본/정규화) + 저자 + 출판사 + 설명에서 검색
 *    초성 검색은 keywordCho가 null이 아닐 때만 적용 (순수 초성 키워드일 때만)
 * 3. 검색 결과 우선순위: 제목 일치(1) > 정규화(2) > 초성(3, 선택적) > 저자(4) > 출판사(5) > 설명(6)
 * 4. 정렬: recommend(추천수) / rental(대출수) / pubdate(출판일) / bookTitle(제목) / regDate(등록일)
 * 5. 페이징 적용 후 PageImpl로 반환
 */