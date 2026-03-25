package com.library.project.library.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "inquiry")
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    private Inquiry inquiry; // Board 대신 Inquiry여야 합니다!

    private String replyText;
    private String replier;

    // [이 메서드가 없어서 오류가 날 수 있습니다]
    public void setInquiry(Inquiry inquiry) {
        this.inquiry = inquiry;
    }

    // [이 메서드가 없어서 오류가 날 수 있습니다]
    public void changeText(String text) {
        this.replyText = text;
    }
}