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