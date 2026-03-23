package com.library.project.library.service;

import com.library.project.library.dto.MemberDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class MemberServiceTests {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 서비스 테스트")
    public void testRegister() {
        log.info("--------------------------------------");
        log.info("1. 회원가입 테스트 시작");

        MemberDTO memberDTO = MemberDTO.builder()
                .mid("tester01")
                .mpw("1111")
                .mname("테스터01")
                .email("tester01@library.com")
                .region("BUSAN")
                .role("USER")
                .build();

        Long id = memberService.register(memberDTO);
        log.info("생성된 회원 내부 PK ID: " + id);
        log.info("--------------------------------------");
    }

    @Test
    @DisplayName("회원 상세조회 테스트")
    public void testReadOne() {
        log.info("--------------------------------------");
        log.info("2. 상세조회 테스트 시작");

        // DB에 이미 있는 아이디를 사용하세요 (testRegister 실행 후 확인 가능)
        String mid = "tester01";

        try {
            MemberDTO memberDTO = memberService.readOne(mid);
            log.info("조회된 회원 정보: " + memberDTO);
        } catch (Exception e) {
            log.error("조회 실패: 해당 아이디가 존재하지 않습니다 -> " + mid);
        }
        log.info("--------------------------------------");
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    public void testModify() {
        log.info("--------------------------------------");
        log.info("3. 정보수정 테스트 시작");

        // 수정을 위해 필요한 최소 데이터: mid(식별자) + 수정할 내용
        MemberDTO memberDTO = MemberDTO.builder()
                .mid("tester01")
                .mname("수정된이름")
                .email("update01@test.com")
                .region("SEOUL")
                .build();

        try {
            memberService.modify(memberDTO);
            log.info("수정 완료! 수정 후 데이터를 다시 조회해봅니다.");

            MemberDTO resultDTO = memberService.readOne("tester01");
            log.info("수정 결과 데이터: " + resultDTO);
        } catch (Exception e) {
            log.error("수정 실패: " + e.getMessage());
        }
        log.info("--------------------------------------");
    }

    @Test
    @DisplayName("회원 탈퇴(삭제) 테스트")
    public void testRemove() {
        log.info("--------------------------------------");
        log.info("4. 삭제 테스트 시작");

        String mid = "tester01";

        try {
            memberService.remove(mid);
            log.info(mid + " 회원 삭제 처리가 완료되었습니다.");
        } catch (Exception e) {
            log.error("삭제 실패: " + e.getMessage());
        }
        log.info("--------------------------------------");
    }



    @Test
    @DisplayName("5. 아이디 찾기 테스트")
    public void testFindId() {
        log.info("--------------------------------------");
        log.info("아이디 찾기 테스트 시작");

        // 가입 시 입력한 이름과 이메일
        String mname = "테스터01";
        String email = "tester01@library.com";

        // 서비스 메서드 호출: findId
        String foundMid = memberService.findId(mname, email);

        if (foundMid != null) {
            log.info("찾은 아이디(mid): " + foundMid);
        } else {
            log.error("아이디 찾기 실패: 일치하는 정보 없음");
        }
        log.info("--------------------------------------");
    }

    @Test
    @DisplayName("6. 비밀번호 찾기(본인확인) 테스트")
    public void testCheckMemberForPw() {
        log.info("--------------------------------------");
        log.info("비밀번호 변경 전 본인확인 테스트");

        String mid = "user1";
        String email = "user1@test.com";

        // 서비스 메서드 호출: checkMemberForPw
        boolean isExists = memberService.checkMemberForPw(mid, email);

        if (isExists) {
            log.info("본인 확인 성공: 비밀번호 변경 단계로 진행 가능");
        } else {
            log.error("본인 확인 실패: 아이디 또는 이메일 불일치");
        }
        log.info("--------------------------------------");
    }

    @Test
    @DisplayName("7. 비밀번호 변경 테스트 (암호화 미적용 단계)")
    public void testUpdatePassword() {
        log.info("--------------------------------------");
        log.info("비밀번호 변경 테스트 시작");

        String mid = "tester01";
        String newPw = "9999"; // 새 비밀번호

        try {
            // 서비스 메서드 호출: updatePassword
            memberService.updatePassword(mid, newPw);
            log.info("비밀번호 변경 로직 실행 완료");

            // 변경 후 실제로 데이터가 바뀌었는지 조회해서 확인
            MemberDTO updatedDTO = memberService.readOne(mid);
            log.info("변경 후 DB에 저장된 비밀번호: " + updatedDTO.getMpw());

        } catch (Exception e) {
            log.error("비밀번호 변경 실패: " + e.getMessage());
        }
        log.info("--------------------------------------");
    }


}