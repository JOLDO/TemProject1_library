package com.library.project.library.service;

import com.library.project.library.dto.WishBookDTO;

public interface WishBookService {
    // 신청서를 등록하고 생성된 번호(wno)를 반환하는 메서드
    Long register(WishBookDTO wishBookDTO);
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