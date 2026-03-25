package com.library.project.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {

    private Long nno;

    @NotEmpty
    @Size(min = 3, max = 100)
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String writer;

    private boolean topFixed;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    // 📍 첨부 파일 이름을 담을 리스트 (이게 있어야 컨트롤러에서 받습니다)
    private List<String> fileNames;
}