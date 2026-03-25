package com.library.project.library.service;

import com.library.project.library.dto.WishBookDTO;

import java.util.List;

public interface WishBookService {
    Long register(WishBookDTO wishBookDTO);

    // 추가: 특정 사용자의 신청 내역을 가져오는 메서드
    List<WishBookDTO> getList(String mid);
}

/*
 * ========== WishBookService 설명 ==========
 * - 역할: 비치희망도서 신청 비즈니스 로직 인터페이스
 * - 구현체: WishBookServiceImpl
 * - 쓰이는 곳: WishBookController에서 주입받아 사용
 *
 * [메서드]
 * - register(): 희망도서 신청서 등록 → 생성된 번호(wno) 반환
 */