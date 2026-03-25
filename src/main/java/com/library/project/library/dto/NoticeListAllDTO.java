package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeListAllDTO {
    private Long nno;
    private String title;
    private String writer;
    private LocalDateTime regDate;

    private Long replyCount; // 댓글 개수

    // 📍 Board 대신 Notice 명칭 사용
    private List<NoticeImageDTO> noticeImages;
}