package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(value = { AuditingEntityListener.class }) // 등록 시간 자동 기록
@Table(name = "wish_book") // 실제 생성될 DB 테이블 이름
public class WishBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wno;             // 고유 번호 (Primary Key, 자동 생성)

    @Column(length = 50, nullable = false)
    private String mid;           // 회원 아이디 (내 서재 필터링용)

    @Column(length = 20, nullable = false)
    private String status;        // 처리 상태 (기본값: 신청중)

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate; // 신청 일자 (JPA Auditing으로 자동 기록)

    // [2] 신청 데이터
    @Column(length = 50, nullable = false)
    private String applicantName; // 신청자 성명

    @Column(length = 20, nullable = false)
    private String wishPhone;     // 신청자 연락처

    @Column(length = 255, nullable = false)
    private String wishBookTitle; // 신청 도서명

    @Column(length = 100, nullable = false)
    private String wishAuthor;    // 저자명

    @Column(length = 100)
    private String wishPublisher; // 출판사명

    // [3] 파일 경로 정보
    @Column(length = 255)
    private String fileName;     // 서버에 저장된 실제 파일 이름 (DB 저장용)

    /**
     * 신청 상태 변경을 위한 메서드 (나중에 관리자 기능 시 사용)
     */
    public void changeStatus(String status) {
        this.status = status;
    }
}

/*
 * ========== WishBookEntity 설명 ==========
 * - 역할: 비치희망도서 신청 정보를 저장하는 엔티티
 * - 쓰이는 곳: WishBookRepository, WishBookServiceImpl, WishBookController에서 사용
 * - DB 테이블명: wish_book
 *
 * [주요 필드]
 * - wno: 신청 고유 번호 (PK)
 * - mid: 신청한 회원 아이디 (내 서재에서 본인 신청 내역 필터링용)
 * - status: 처리 상태 (신청중 / 심사중 / 구입중 / 정리중 / 이용가능 / 반려)
 * - regDate: 신청 일자 (JPA Auditing 자동 기록)
 * - applicantName / wishPhone: 신청자 정보
 * - wishBookTitle / wishAuthor / wishPublisher: 희망 도서 정보
 * - fileName: 업로드된 도서 이미지 파일명
 *
 * [메서드]
 * - changeStatus(): 관리자가 신청 상태를 변경할 때 사용
 */
