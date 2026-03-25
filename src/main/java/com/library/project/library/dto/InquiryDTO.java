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

    private Long ino; // nno 대신 ino 사용

    @NotEmpty
    @Size(min = 3, max = 100)
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String writer;

    // 질의응답 핵심 필드: 비밀글 여부 (Notice의 topFixed 역할)
    private boolean secret;

    // 질의응답 핵심 필드: 답변 완료 여부
    private boolean answered;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}