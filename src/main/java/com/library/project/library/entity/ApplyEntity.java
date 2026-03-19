package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "apply")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ano; // 대관 신청 고유 번호

    // [추가] 내 서재 조회를 위한 핵심 컬럼
    @Column(length = 50, nullable = false)
    private String mid; // 회원 아이디 (로그인한 사용자의 신청 내역을 불러오는 기준점)

    @Column(length = 50, nullable = false)
    private String applicantName; // 신청자 성명

    @Column(length = 20, nullable = false)
    private String phone; // 신청자 연락처

    @Column(length = 200, nullable = false)
    private String eventName; // 행사명

    @Column(length = 50, nullable = false)
    private String facilityType; // 대관 시설 종류

    @Column(nullable = false)
    private Integer participants; // 예상 인원

    @Column(nullable = false)
    private LocalDate applyDate; // 시설 이용 희망일

    @Column(length = 20, nullable = false)
    private String applyTime; // 시설 이용 희망 시간대

    @Builder.Default
    @Column(columnDefinition = "TEXT", nullable = false)
    private String eventContent = "상세 내용 없음"; // 행사 상세 내용

    @Builder.Default
    @Column(columnDefinition = "TEXT", nullable = false)
    private String inquiryContent = "문의사항 없음";

    @Column(updatable = false)
    private LocalDateTime regDate; // 신청 접수 일시

    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
        if (this.eventContent == null || this.eventContent.trim().isEmpty()) {
            this.eventContent = "상세 내용 없음";
        }
    }
}