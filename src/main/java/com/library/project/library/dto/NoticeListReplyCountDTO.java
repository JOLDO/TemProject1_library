package com.library.project.library.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeListReplyCountDTO {

    // 목록 화면에 보이는 컬럼의 목록. + 추가, 댓글의 갯수
    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate;
    // 추가
    private Long replyCount;

}
