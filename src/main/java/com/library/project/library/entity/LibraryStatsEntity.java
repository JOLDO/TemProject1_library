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