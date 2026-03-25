package com.library.project.library.dto;

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
public class ApplyDTO {
    private Long ano;             // 신청 번호
    private String mid;           // 신청자 id
    private String applicantName; // 신청자
    private String phone;         // 연락처
    private String eventName;     // 행사명
    private String facilityType;  // 시설 종류
    private Integer participants; // 참석 인원
    private LocalDate applyDate;  // 신청 날짜
    private String applyTime;     // 시간대 (오전/오후/야간)
    private String eventContent;  // 행사 상세 내용
    private String inquiryContent;  // 문의 내용
    private LocalDateTime regDate; // 등록 시간
}

/*
 * ========== ApplyDTO 설명 ==========
 * - 역할: 시설 대관 신청 데이터를 화면에서 서버로 전달하는 DTO
 * - 쓰이는 곳: ApplyController, ApplyServiceImpl에서 사용
 *
 * [주요 필드]
 * - mid: 신청자 회원 아이디
 * - applicantName / phone: 신청자 정보
 * - eventName: 행사명
 * - facilityType: 시설 종류
 * - participants: 참석 인원
 * - applyDate / applyTime: 희망 일시
 * - eventContent: 행사 상세 내용
 * - inquiryContent: 문의사항
 */