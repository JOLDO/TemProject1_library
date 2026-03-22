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
public class EventDTO {
    private Long id;
    private String category;
    private String title;
    private String content;
    private LocalDateTime eventDate;
    private String place;
    private String extraInfo;
    private String imageName;
    private String status;
}

/*
 * ========== EventDTO 설명 ==========
 * - 역할: 행사/강좌/영화상영 정보를 화면과 주고받기 위한 DTO
 * - 쓰이는 곳: EventController, EventServiceImpl에서 사용
 *
 * [주요 필드]
 * - id: 행사 PK
 * - category: 분류 (G:강좌, M:영화상영, E:행사)
 * - title / content: 행사 제목 / 상세 내용
 * - eventDate: 행사 일시
 * - place: 장소
 * - extraInfo: 부가 정보
 * - imageName: 행사 이미지 파일명
 * - status: 진행 상태
 */