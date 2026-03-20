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