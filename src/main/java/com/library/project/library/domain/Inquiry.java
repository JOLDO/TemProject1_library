package com.library.project.library.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "tbl_inquiry") // 직접 만드신 테이블 이름과 매핑
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino; // 문의사항 번호 (Notice의 nno 역할)

    @Column(length = 500, nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;

    // 비밀글 여부 (true면 작성자와 관리자만 볼 수 있게 처리)
    private boolean secret;

    // 답변 완료 여부 (답변이 달리면 true로 변경)
    private boolean answered;

    /**
     * 수정을 위한 메서드
     * 공지사항의 topFixed 대신 secret(비밀글 여부)을 수정할 수 있게 합니다.
     */
    public void change(String title, String content, boolean secret) {
        this.title = title;
        this.content = content;
        this.secret = secret;
    }

    // 기존 changeAnswered를 changeAnswerStatus로 변경
    public void changeAnswerStatus(boolean answered) {
        this.answered = answered;
    }
}