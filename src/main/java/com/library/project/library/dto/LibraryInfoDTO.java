package com.library.project.library.dto;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryInfoDTO {

    private Long id; // info_id 매핑용
    private String libraryName;
    private String address;
    private String contact;
    private String donationGuide;

    // Lombok @Builder가 자동으로 아래와 같은 builder() 메서드를 생성해줍니다.
}

/*
 * ========== LibraryInfoDTO 설명 ==========
 * - 역할: 도서관 기본 정보를 화면에 전달하는 DTO
 * - 쓰이는 곳: InfoController, InfoServiceImpl에서 사용
 *
 * [주요 필드]
 * - id: 도서관 정보 PK (info_id 매핑)
 * - libraryName: 도서관 이름
 * - address: 주소
 * - contact: 연락처
 * - donationGuide: 기증/납본 안내 텍스트
 */