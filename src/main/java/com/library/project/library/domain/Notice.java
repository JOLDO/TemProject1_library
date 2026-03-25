package com.library.project.library.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nno;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;

    private boolean topFixed;

    // 📍 조회수 필드 추가 및 기본값 0 설정
    @Builder.Default
    @Column(nullable = false)
    private int visit_count = 0;

    // 서비스에서 호출할 때 필요한 수정 메서드
    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 첨부파일 이미지 처리
    @OneToMany(mappedBy = "notice",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    private Set<NoticeImage> imageSet = new HashSet<>();

    public void addImage(String uuid, String fileName) {
        NoticeImage noticeImage = NoticeImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .notice(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(noticeImage);
    }

    public void clearImages() {
        if(imageSet != null) {
            imageSet.forEach(noticeImage -> noticeImage.changeNotice(null));
            this.imageSet.clear();
        }
    }



}