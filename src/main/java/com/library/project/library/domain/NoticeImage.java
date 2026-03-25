package com.library.project.library.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "notice") // 무한 루프 방지
public class NoticeImage implements Comparable<NoticeImage> {

    @Id
    private String uuid;

    private String fileName;

    private int ord;

    @ManyToOne(fetch = FetchType.LAZY)
    private Notice notice; // 📍 Notice 엔티티와 연결되는 핵심 필드

    public void changeNotice(Notice notice) {
        this.notice = notice;
    }

    @Override
    public int compareTo(NoticeImage other) {
        return this.ord - other.ord;
    }
}