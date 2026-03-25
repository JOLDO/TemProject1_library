package com.library.project.library.domain;

import jakarta.persistence.*;
import lombok.*;
import com.library.project.library.entity.Member;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
@Table(name = "tbl_inquiry")
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    /**
     * 📍 cascade = CascadeType.MERGE 추가
     * DB에서 조회해온 Member 객체와 현재 저장하려는 Inquiry 사이의
     * 영속성 상태를 안전하게 합쳐주는 역할을 합니다. (Transient 에러 방지)
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "mid", referencedColumnName = "mid")
    private Member member;

    private boolean secret;

    private boolean answered;

    public void change(String title, String content, boolean secret) {
        this.title = title;
        this.content = content;
        this.secret = secret;
    }

    public void changeAnswerStatus(boolean answered) {
        this.answered = answered;
    }
}