package com.library.project.library.service;

import com.library.project.library.dto.WishBookDTO;

public interface WishBookService {
    // 신청서를 등록하고 생성된 번호(wno)를 반환하는 메서드
    Long register(WishBookDTO wishBookDTO);
}