package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "library_stats")
@Getter
@Builder // 👈 빌더가 모든 필드를 인식하려면 아래 두 어노테이션이 필요합니다.
@AllArgsConstructor
@NoArgsConstructor
public class LibraryStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statId;

    private String categoryName;

    private Long itemCount;

    // 1. 이 필드가 있는지 꼭 확인하세요! (에러의 핵심 원인)
    private Long infoId;

    // 2. 수정을 위한 change 메서드 (서비스의 modifyStat에서 사용됨)
    public void changeCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void changeItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }
}

/*
 * ========== LibraryStatsEntity 설명 ==========
 * - 역할: 도서관 자료 현황(카테고리별 보유 수량) 통계를 저장하는 엔티티
 * - 쓰이는 곳: LibraryStatsRepository, InfoServiceImpl, InfoController에서 사용
 * - DB 테이블명: library_stats
 *
 * [주요 필드]
 * - statId: 통계 PK
 * - categoryName: 자료 분류명 (예: 총류, 철학, 사회과학 등)
 * - itemCount: 해당 분류의 보유 자료 수
 * - infoId: 연결된 도서관 정보 ID (현재 1L 고정)
 *
 * [메서드]
 * - changeCategoryName(): 분류명 수정 (InfoServiceImpl.modifyStat()에서 호출)
 * - changeItemCount(): 보유 수량 수정 (InfoServiceImpl.modifyStat()에서 호출)
 */