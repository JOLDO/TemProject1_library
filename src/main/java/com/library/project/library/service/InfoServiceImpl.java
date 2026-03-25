package com.library.project.library.service;

import com.library.project.library.dto.LibraryInfoDTO;
import com.library.project.library.dto.LibraryStatsDTO;
import com.library.project.library.entity.LibraryStatsEntity;
import com.library.project.library.repository.InfoRepository;
import com.library.project.library.repository.LibraryStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InfoServiceImpl implements InfoService {

    private final InfoRepository infoRepository;
    private final LibraryStatsRepository statsRepository;

    @Override
    @Transactional(readOnly = true)
    public LibraryInfoDTO getStaticLibraryInfo() {
        return infoRepository.findById(1L)
                .map(entity -> LibraryInfoDTO.builder()
                        .libraryName(entity.getLibraryName())
                        .address(entity.getAddress())
                        .contact(entity.getContact())
                        .donationGuide(entity.getDonationGuide())
                        .build())
                .orElseGet(LibraryInfoDTO::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryStatsDTO> getLibraryStatistics() {
        return statsRepository.findAll().stream()
                .map(entity -> LibraryStatsDTO.builder()
                        .statId(entity.getStatId())
                        .categoryName(entity.getCategoryName())
                        .itemCount(Math.toIntExact(entity.getItemCount()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void registerStat(LibraryStatsDTO dto) {
        LibraryStatsEntity entity = LibraryStatsEntity.builder()
                .categoryName(dto.getCategoryName())
                .itemCount((long) dto.getItemCount())
                .infoId(1L) // 1번 도서관 데이터로 고정
                .build();
        statsRepository.save(entity);
    }

    @Override
    public void modifyStat(LibraryStatsDTO dto) {
        statsRepository.findById(dto.getStatId()).ifPresent(entity -> {
            entity.changeCategoryName(dto.getCategoryName());
            entity.changeItemCount((long) dto.getItemCount());
        });
    }

    @Override
    public void removeStat(Long statId) {
        statsRepository.deleteById(statId);
    }

    // 빌드 에러 해결을 위한 메서드 추가 (오버라이딩)
    public LibraryStatsDTO getStat(Long statId) {
        Optional<LibraryStatsEntity> result = statsRepository.findById(statId);
        LibraryStatsEntity entity = result.orElseThrow();

        // ModelMapper 없이 수동으로 DTO 만들기 (에러 즉시 해결됨)
        return LibraryStatsDTO.builder()
                .statId(entity.getStatId())
                .categoryName(entity.getCategoryName())
                .itemCount(Math.toIntExact(entity.getItemCount()))
                .build();
    }
}


/*
 * ========== InfoServiceImpl 설명 ==========
 * - 역할: InfoService 인터페이스의 구현체. 도서관 정보 + 자료 현황 CRUD
 * - 쓰이는 곳: InfoController에서 주입받아 사용
 *
 * [메서드]
 * - getStaticLibraryInfo(): id=1L 고정으로 도서관 정보 조회 → LibraryInfoDTO 반환
 * - getLibraryStatistics(): 전체 통계 조회 → LibraryStatsDTO 리스트 반환
 * - registerStat(): DTO → Entity 변환 후 DB 저장 (infoId=1L 고정)
 * - modifyStat(): statId로 Entity 찾은 후 changeCategoryName/changeItemCount로 수정 (Dirty Checking)
 * - removeStat(): statId로 삭제
 */
