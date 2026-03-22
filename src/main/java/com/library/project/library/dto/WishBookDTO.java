package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishBookDTO {

    // [1] 시스템 관리 및 조회용 (마이페이지/내 서재 기능 필수)
    private Long wno;             // 고유 번호 (Primary Key)
    private String mid;           // 회원 아이디 (본인 신청 내역 필터링용)
    private String status;        // 처리 상태 (신청중 / 심사중 / 구입중 / 정리중 / 이용가능 / 반려)
    private LocalDateTime regDate; // 신청 일자 (신청일 순 정렬 및 조회용)

    // [2] 신청자 및 도서 정보 (사용자 입력 데이터)
    private String wishApplicantName; // 신청자 성명
    private String wishPhone;     // 신청자 연락처 (안내 문자 발송용)
    private String wishBookTitle; // 신청 도서명
    private String wishAuthor;    // 저자명
    private String wishPublisher; // 출판사명

    // [3] 파일 처리 데이터 (이미지 업로드 관련)
    private MultipartFile wishBookImage; // 화면에서 전송된 실제 이미지 파일 (Binary)
    private String fileName;             // 서버에 저장된 파일 이름 (DB 저장 및 이미지 출력용)
}

/*
 * ========== WishBookDTO 설명 ==========
 * - 역할: 비치희망도서 신청 데이터를 화면과 주고받는 DTO
 * - 쓰이는 곳: WishBookController, WishBookServiceImpl에서 사용
 *
 * [주요 필드]
 * - wno: 신청 고유 번호 (PK)
 * - mid: 회원 아이디 (본인 신청 내역 필터링용)
 * - status: 처리 상태 (신청중~이용가능/반려)
 * - regDate: 신청 일자
 * - wishApplicantName / wishPhone: 신청자 정보
 * - wishBookTitle / wishAuthor / wishPublisher: 희망 도서 정보
 * - wishBookImage: 화면에서 업로드한 이미지 파일 (MultipartFile)
 * - fileName: 서버에 저장된 파일명 (DB 저장용)
 */