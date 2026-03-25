package com.library.project.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDTO {

    private Long ino;

    @NotEmpty(message = "제목은 필수 입력 사항입니다.")
    @Size(min = 3, max = 100, message = "제목은 3자 이상 100자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "내용은 필수 입력 사항입니다.")
    private String content;

    /**
     * 📍 [핵심 수정] writer 대신 mid 사용
     * 컨트롤러에서 세션의 로그인 아이디를 이 필드에 주입합니다.
     * @NotEmpty를 통해 작성자 정보가 누락되지 않도록 방어합니다.
     */
    @NotEmpty(message = "작성자 아이디는 필수입니다.")
    private String mid;

    // 비밀글 여부 (true: 작성자와 관리자만 확인 가능)
    private boolean secret;

    // 답변 완료 여부 (관리자 답변 시 업데이트)
    private boolean answered;

    // BaseEntity에서 상속받은 날짜 정보들
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}