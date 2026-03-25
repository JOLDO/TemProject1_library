package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryListReplyCountDTO {
    private Long ino;
    private String title;
    private String writer;
    private LocalDateTime regDate;
    private boolean secret;
    private boolean answered;
    private Long replyCount; // as("replyCount")와 일치해야 함
}