package com.library.project.library.service;

import com.library.project.library.dto.ApplyDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ApplyServiceTests {

    @Autowired
    private ApplyService applyService;

    @Test
    @DisplayName("서비스 계층 - 내 신청 내역 조회 결과 확인")
    public void testGetApplyList() {
        // DB에 존재하는 테스트용 사용자 ID
        String mid = "test_user";

        List<ApplyDTO> list = applyService.getApplyListByMid(mid);

        System.out.println("------------------------------------");
        if (list.isEmpty()) {
            System.out.println("해당 사용자의 신청 내역이 없습니다.");
        } else {
            list.forEach(dto -> {
                System.out.println("번호: " + dto.getAno());
                System.out.println("신청 행사: " + dto.getEventName());
                System.out.println("신청자명: " + dto.getApplicantName());
                System.out.println("--------------------------------");
            });
        }
    }
}