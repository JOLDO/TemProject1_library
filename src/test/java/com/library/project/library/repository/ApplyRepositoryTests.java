package com.library.project.library.repository;

import com.library.project.library.entity.ApplyEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class ApplyRepositoryTests {

    @Autowired
    private ApplyRepository applyRepository;

    @Test
    @DisplayName("회원 아이디로 신청 내역 조회 테스트")
    @Transactional // 테스트 후 롤백을 위해 권장 (조회만 할 때는 선택사항)
    public void testFindByMid() {
        // 1. 테스트용 아이디 설정 (DB에 있는 ID나 'test_user' 사용)
        String mid = "test_user";

        // 2. 조회 실행
        List<ApplyEntity> result = applyRepository.findByMid(mid);

        // 3. 결과 출력
        System.out.println("========= 조회 결과 =========");
        if (result.isEmpty()) {
            System.out.println("해당 아이디의 신청 내역이 없습니다.");
        } else {
            result.forEach(apply -> {
                System.out.println("신청번호: " + apply.getAno());
                System.out.println("신청자: " + apply.getApplicantName());
                System.out.println("행사명: " + apply.getEventName());
                System.out.println("신청일: " + apply.getRegDate());
                System.out.println("---------------------------");
            });
        }
        System.out.println("============================");
    }
}
