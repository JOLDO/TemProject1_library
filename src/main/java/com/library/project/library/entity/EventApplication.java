package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"event", "member"}) // 무한 참조 방지!
@EntityListeners(value = { AuditingEntityListener.class })
public class EventApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 행사에 신청했는지?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // 누가 신청했는지? (Member 엔티티와 연결!)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(name = "apply_date", updatable = false)
    private LocalDateTime applyDate;

    @Builder.Default
    private String status = "신청완료"; // 기본값 설정
}