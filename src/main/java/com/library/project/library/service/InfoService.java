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