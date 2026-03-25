package com.library.project.library.service;

import com.library.project.library.dto.MemberDTO;
import com.library.project.library.entity.Member;

import java.util.List;

public interface MemberService {
    // 회원가입 (반환값은 생성된 id)
    Long register(MemberDTO memberDTO);

    // 회원 상세 조회 (아이디로 조회)
    MemberDTO readOne(String mid);

    // 회원 정보 수정
    void modify(MemberDTO memberDTO);

    // 회원 탈퇴
    void remove(String mid);

    // 아이디 중복 체크
    boolean checkId(String mid);

    // 이메일 중복 체크
    boolean checkEmail(String email);

    // 20260320 아이디/비밀번호 찾기 추가
    String findId(String mname, String email);           // 1. 아이디 찾기
    boolean checkMemberForPw(String mid, String email);    // 2. 비번 찾기 전 확인
    void updatePassword(String mid, String newPw);         // 3. 비번 변경 (암호화 없이)

    List<MemberDTO> searchMembers(String keyword);

}

/*
 * ========== MemberService 설명 ==========
 * - 역할: 회원 관련 비즈니스 로직 인터페이스
 * - 구현체: MemberServiceImpl
 * - 쓰이는 곳: MemberController에서 주입받아 사용
 *
 * [메서드]
 * - register(): 회원가입 → 회원가입 폼 POST 처리
 * - readOne(): 아이디로 회원 조회 → 로그인, 마이페이지, 수정 화면에서 사용
 * - modify(): 회원 정보 수정 → 마이페이지 수정 POST 처리
 * - remove(): 회원 탈퇴 → 회원 삭제
 * - checkId(): 아이디 중복 체크 → 회원가입 시 AJAX 호출
 * - checkEmail(): 이메일 중복 체크 → 회원가입 시 AJAX 호출
 * - findId(): 아이디 찾기 → 이름+이메일로 아이디 조회
 * - checkMemberForPw(): 비밀번호 찾기 전 본인확인 → 아이디+이메일 일치 확인
 * - updatePassword(): 비밀번호 변경 → 비밀번호 재설정
 */