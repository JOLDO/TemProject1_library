package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"event", "member"})
public class EventApply extends BaseEntity { // 등록시간 처리를 위해 BaseEntity 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Event event; // 신청한 행사

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; // 신청한 회원
}