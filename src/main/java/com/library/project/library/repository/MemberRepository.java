package com.library.project.library.repository;

import com.library.project.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 로그인 아이디(mid)로 회원 한 명을 조회하는 메서드
    Optional<Member> findByMid(String mid);

    // 회원가입 시 아이디 중복 확인을 위한 메서드
    boolean existsByMid(String mid);

    // 이메일 존재 여부를 위해 추가
    boolean existsByEmail(String email);

    // 20260320 아이디/비밀번호 찾기 추가
    // 아이디 찾기: 이름과 이메일이 일치하는 회원 조회
    Optional<Member> findByMnameAndEmail(String mname, String email);
    // 비밀번호 찾기(본인확인): 아이디와 이메일이 일치하는 회원 조회
    Optional<Member> findByMidAndEmail(String mid, String email);

}

/*
 * ========== MemberRepository 설명 ==========
 * - 역할: Member 엔티티의 DB 접근을 담당하는 리포지토리
 * - 쓰이는 곳: MemberServiceImpl, RentalService에서 사용
 *
 * [메서드]
 * - findByMid(): 로그인 아이디로 회원 조회 → 로그인, 마이페이지, 정보수정, 탈퇴 등에서 사용
 * - existsByMid(): 아이디 중복 확인 → 회원가입 시 AJAX 중복 체크
 * - existsByEmail(): 이메일 중복 확인 → 회원가입 시 AJAX 중복 체크
 * - findByMnameAndEmail(): 이름+이메일로 회원 조회 → 아이디 찾기 기능
 * - findByMidAndEmail(): 아이디+이메일로 회원 조회 → 비밀번호 찾기 전 본인확인
 */