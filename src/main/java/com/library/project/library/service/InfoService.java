package com.library.project.library.service;

import com.library.project.library.dto.LibraryInfoDTO;
import com.library.project.library.dto.LibraryStatsDTO;
import java.util.List;

public interface InfoService {
    LibraryInfoDTO getStaticLibraryInfo();

    List<LibraryStatsDTO> getLibraryStatistics();

    // CRUD 추가
    void registerStat(LibraryStatsDTO dto); // 등록

    void modifyStat(LibraryStatsDTO dto);   // 수정

    void removeStat(Long statId);           // 삭제


    // 수정 처리를 위한 메서드도 미리 선언해두면 좋습니다.
}

/*
 * ========== InfoService 설명 ==========
 * - 역할: 도서관 정보 및 자료 현황 통계 비즈니스 로직 인터페이스
 * - 구현체: InfoServiceImpl
 * - 쓰이는 곳: InfoController에서 주입받아 사용
 *
 * [메서드]
 * - getStaticLibraryInfo(): 도서관 기본 정보 조회 (이름, 주소, 연락처 등)
 * - getLibraryStatistics(): 자료 현황 통계 전체 조회 (카테고리별 보유 수량)
 * - registerStat(): 자료 현황 항목 등록
 * - modifyStat(): 자료 현황 항목 수정
 * - removeStat(): 자료 현황 항목 삭제
 */