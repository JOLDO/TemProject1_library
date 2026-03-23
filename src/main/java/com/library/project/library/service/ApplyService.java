package com.library.project.library.service;


import com.library.project.library.dto.ApplyDTO;
import com.library.project.library.entity.ApplyEntity;

import java.util.List;

public interface ApplyService {
    Long register(ApplyDTO applyDTO);

    // 1. 사용자 아이디(mid)로 내 신청 리스트 가져오기
    List<ApplyDTO> getApplyListByMid(String mid);

    // 2. Entity를 DTO로 변환하는 공통 메서드 (default)
    default ApplyDTO entityToDto(ApplyEntity entity) {
        return ApplyDTO.builder()
                .ano(entity.getAno())
                .mid(entity.getMid())
                .applicantName(entity.getApplicantName())
                .phone(entity.getPhone())
                .eventName(entity.getEventName())
                .facilityType(entity.getFacilityType())
                .participants(entity.getParticipants())
                .applyDate(entity.getApplyDate())
                .applyTime(entity.getApplyTime())
                .eventContent(entity.getEventContent())
                .inquiryContent(entity.getInquiryContent())
                .regDate(entity.getRegDate())
                .build();
    }

    ApplyDTO getApply(Long ano);


}

/*
 * ========== ApplyService 설명 ==========
 * - 역할: 시설 대관 신청 비즈니스 로직 인터페이스
 * - 구현체: ApplyServiceImpl
 * - 쓰이는 곳: ApplyController에서 주입받아 사용
 *
 * [메서드]
 * - register(): 대관 신청서 등록 → 생성된 신청 번호(ano) 반환
 */