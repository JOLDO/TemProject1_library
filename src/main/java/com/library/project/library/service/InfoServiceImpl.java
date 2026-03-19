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
}