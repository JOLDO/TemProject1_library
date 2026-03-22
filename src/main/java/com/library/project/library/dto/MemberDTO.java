package com.library.project.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

    private Long id; // 내부 관리용 PK (id)

    @NotEmpty
    @Size(min = 4, max = 20)
    private String mid; // 로그인 아이디

    @NotEmpty
    private String mpw; // 비밀번호

    @NotEmpty
    private String mname; // 이름

    @Email
    @NotEmpty
    private String email; // 이메일

    private String region; // 지역

    private String role; // Role (USER, ADMIN)

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}

/*
 * ========== MemberDTO 설명 ==========
 * - 역할: 회원 정보를 화면과 주고받기 위한 DTO (회원가입, 로그인, 마이페이지 등)
 * - 쓰이는 곳: MemberController, MemberServiceImpl에서 사용
 *
 * [주요 필드]
 * - id: 내부 PK
 * - mid: 로그인 아이디 (@NotEmpty, 4~20자)
 * - mpw: 비밀번호 (@NotEmpty)
 * - mname: 이름 (@NotEmpty)
 * - email: 이메일 (@Email, @NotEmpty)
 * - region: 지역
 * - role: 권한 (USER / ADMIN)
 * - regDate / modDate: 가입일 / 수정일
 *
 * [유효성 검증]
 * - 회원가입/수정 시 @Valid로 검증 → 실패 시 BindingResult로 에러 처리
 */