package com.library.project.library.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@Log4j2
@Transactional
class RecommendRepositoryTests {

    @Autowired private RecommendRepository recommendRepository;

    @Test
    @DisplayName("추천 여부 확인 - 추천한 책")
    void existsByBookIdAndMemberId_true() {
        Long bookId = 732L;
        Long memberId = 4L;
        boolean result = recommendRepository.existsByBook_IdAndMember_Id(
                bookId, memberId);
        log.info("추천 여부 확인 - 추천한 책 : " + result);
    }

    @Test
    @DisplayName("추천 여부 확인 - 추천 안 한 책")
    void existsByBookIdAndMemberId_false() {
        Long bookId = 701L;
        Long memberId = 4L;
        boolean result = recommendRepository.existsByBook_IdAndMember_Id(
                bookId, memberId);
        log.info("추천 여부 확인 - 추천 안 한 책 : " + result);
    }

    @Test
    @DisplayName("ISBN 목록으로 추천한 ISBN만 배치 조회")
    void findBookIdsByBookIsbnIn() {
        List<String> isbns = List.of("9791157957880", "9788937460470");
        Long memberId = 4L;
        List<String> result = recommendRepository.findBookIdsByBookIsbnIn(
                isbns, memberId);
        log.info("ISBN 목록으로 추천한 ISBN만 배치 조회 : " + result);
    }

    @Test
    @DisplayName("추천 수 집계")
    void countByBookId() {
        Long bookId = 676L;
        int count = recommendRepository.countByBook_Id(bookId);
        log.info("추천 수 집계 : " + count);

    }

    @Test
    @DisplayName("추천 안 한 책 추천 수 0")
    void countByBookId_zero() {
        Long bookId = 701L;
        int count = recommendRepository.countByBook_Id(bookId);
        log.info("추천 안 한 책 추천 수 0 : " + count);
    }
}