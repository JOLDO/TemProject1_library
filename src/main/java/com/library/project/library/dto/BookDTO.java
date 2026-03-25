package com.library.project.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.project.library.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;
    private String isbn;
    private String bookTitle;
    private String bookImage;
    private String author;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pubdate;
    private String description;
    private String bookTitleNormal;
    private String bookTitleChosung;
    private BookStatus status;
    private Boolean recommended;
    private Boolean requestPending;
    private Boolean rentedByMe;     // 현재 로그인한 회원이 이 isbn의 책을 대여중인지 여부 → 대여중이면 예약 버튼 비활성화
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime modDate;
}

/*
 * ========== BookDTO 설명 ==========
 * - 역할: 도서 정보를 화면(View)과 주고받기 위한 데이터 전송 객체
 * - 쓰이는 곳: BookController, BookRestController, BookServiceImpl에서 사용
 *
 * [주요 필드]
 * - id ~ description: Book 엔티티와 동일한 도서 기본 정보
 * - bookTitleNormal / bookTitleChosung: 한글 검색을 위한 정규화/초성 제목
 * - status: 대여 가능 여부 (AVAILABLE / RENTED) - isbn 전체 기준으로 판단
 * - recommended: 추천 여부 (true/false) - 프론트에서 추천 버튼 초기 상태 결정에 사용
 * - requestPending: 예약 대기 여부 (true/false) - 프론트에서 예약 중 뱃지 표시에 사용
 * - rentedByMe: 내가 대여중인지 여부 (true/false) - 대여중이면 예약 버튼 비활성화
 * - regDate / modDate: 등록일/수정일 (JSON 응답 시 yyyy-MM-dd 포맷)
 */