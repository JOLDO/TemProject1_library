package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryStatsDTO {
    private Long statId;
    private String categoryName;
    private int itemCount;
    private String lastUpdated;
    private String remarks;
    private Long infoId;
}

/*
 * ========== LibraryStatsDTO 설명 ==========
 * - 역할: 도서관 자료 현황 통계를 화면과 주고받는 DTO
 * - 쓰이는 곳: InfoController, InfoServiceImpl에서 사용 (등록/수정/삭제/조회)
 *
 * [주요 필드]
 * - statId: 통계 PK
 * - categoryName: 자료 분류명
 * - itemCount: 보유 자료 수
 * - lastUpdated: 마지막 업데이트 일시
 * - remarks: 비고
 * - infoId: 연결된 도서관 정보 ID
 */
