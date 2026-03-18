package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 PK

    @Column(length = 50, nullable = false, unique = true)
    private String mid; // 로그인 아이디

    @Column(length = 100, nullable = false)
    private String mpw; // 비밀번호

    @Column(length = 50, nullable = false)
    private String mname; // 이름

    private String email;
    private String region;

    @Enumerated(EnumType.STRING)
    private Role role; // [USER, ADMIN]

    public enum Role {
        USER, ADMIN
    }

    // 눈에 보이지 않지만, BaseEntity를 이용해서, regDate, modDate 도 추가가 될 예정.

    // 정보 수정 메서드 (내 서재/마이페이지용)
    public void change(String mname, String email, String region) {
        this.mname = mname;
        this.email = email;
        this.region = region;
    }
}