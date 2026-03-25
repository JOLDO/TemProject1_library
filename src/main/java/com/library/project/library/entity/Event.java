package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "library_event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB에 category라고 만들었으니까 자바 변수명과 자동으로 매칭돼!
    @Column(nullable = false)
    private String category; // 분류 (G:강좌, M:영화상영, E:행사)

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 10)
    private String status;

    private LocalDateTime eventDate;

    private String place;

    private String extraInfo;

    private String imageName;

    public void change(String title, String content, String imageName) {
        this.title = title;
        this.content = content;
        this.imageName = imageName;
    }
}

/*
 * ========== Event 엔티티 설명 ==========
 * - 역할: 도서관 행사/강좌/영화상영 정보를 저장하는 엔티티
 * - 쓰이는 곳: EventRepository, EventServiceImpl, EventController에서 사용
 * - DB 테이블명: library_event
 *
 * [주요 필드]
 * - id: 행사 PK
 * - category: 분류 코드 (G:강좌, M:영화상영, E:행사)
 * - title: 행사 제목
 * - content: 행사 상세 내용
 * - status: 진행 상태
 * - eventDate: 행사 일시
 * - place: 장소
 * - extraInfo: 부가 정보
 * - imageName: 행사 이미지 파일명
 *
 * [메서드]
 * - change(): 행사 정보 수정 (제목, 내용, 이미지 변경)
 */