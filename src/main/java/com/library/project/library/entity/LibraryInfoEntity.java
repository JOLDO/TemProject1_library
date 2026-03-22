package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "library_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LibraryInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id") // image_c8705e.png 확인: PK 컬럼명은 info_id입니다.
    private Long id;

    @Column(name = "library_name", length = 255)
    private String libraryName;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "contact", length = 50)
    private String contact;

    @Column(name = "donation_guide", columnDefinition = "TEXT")
    private String donationGuide;
}

/*
 * ========== LibraryInfoEntity 설명 ==========
 * - 역할: 도서관 기본 정보(이름, 주소, 연락처, 기증안내)를 저장하는 엔티티
 * - 쓰이는 곳: InfoRepository, InfoServiceImpl에서 사용
 * - DB 테이블명: library_info
 *
 * [주요 필드]
 * - id: 도서관 정보 PK (info_id)
 * - libraryName: 도서관 이름
 * - address: 도서관 주소
 * - contact: 연락처
 * - donationGuide: 기증/납본 안내 텍스트
 *
 * [참고]
 * - 현재 id=1L 고정으로 한 개의 도서관 정보만 사용 중
 */