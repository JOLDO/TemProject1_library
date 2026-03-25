package com.library.project.library.service;

import com.library.project.library.dto.LibraryStatsDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InfoServiceTests {

    @Autowired
    private InfoService infoService;

    @Test
    @DisplayName("자료 현황 등록 및 조회 테스트")
    public void testRegisterAndList() {
        // 1. 등록할 데이터 준비
        LibraryStatsDTO dto = LibraryStatsDTO.builder()
                .categoryName("테스트 도서")
                .itemCount(1)
                .infoId(1L)
                .build();

        // 2. 등록 실행
        infoService.registerStat(dto);

        // 3. 전체 목록 조회 후 방금 넣은 데이터가 있는지 확인
        List<LibraryStatsDTO> statsList = infoService.getLibraryStatistics();

        boolean found = statsList.stream()
                .anyMatch(s -> s.getCategoryName().equals("테스트 도서"));

        assertThat(found).isTrue();
    }

    @Test
    @DisplayName("자료 현황 수정 테스트")
    @Transactional // 테스트 후 DB를 깨끗하게 유지하기 위해 사용
    public void testModify() {
        // 1. 수정할 기존 데이터 가져오기 (첫 번째 데이터 기준)
        List<LibraryStatsDTO> statsList = infoService.getLibraryStatistics();
        LibraryStatsDTO target = statsList.get(0);
        Long targetId = target.getStatId();
        String newName = "수정된 카테고리";

        // 2. 데이터 수정 세팅
        target.setCategoryName(newName);
        target.setItemCount(5);

        // 3. 수정 실행
        infoService.modifyStat(target);

        // 4. 다시 조회해서 값이 바뀌었는지 검증
        List<LibraryStatsDTO> updatedList = infoService.getLibraryStatistics();
        LibraryStatsDTO result = updatedList.stream()
                .filter(s -> s.getStatId().equals(targetId))
                .findFirst()
                .orElseThrow();

        assertThat(result.getCategoryName()).isEqualTo(newName);
        assertThat(result.getItemCount()).isEqualTo(999L);
    }

    @Test
    @DisplayName("자료 현황 삭제 테스트")
    public void testRemove() {
        // 1. 삭제할 대상 확인 (마지막 데이터 기준)
        List<LibraryStatsDTO> statsList = infoService.getLibraryStatistics();
        int beforeSize = statsList.size();
        Long targetId = statsList.get(beforeSize - 1).getStatId();

        // 2. 삭제 실행
        infoService.removeStat(targetId);

        // 3. 전체 개수가 줄어들었는지 확인
        List<LibraryStatsDTO> afterList = infoService.getLibraryStatistics();
        assertThat(afterList.size()).isEqualTo(beforeSize - 1);
    }
}