package com.library.project.library.service;


import com.library.project.library.dto.ApplyDTO;
import com.library.project.library.entity.ApplyEntity;
import com.library.project.library.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ApplyServiceImpl implements ApplyService {

    private final ApplyRepository applyRepository;

    @Override
    public Long register(ApplyDTO applyDTO) {
        // DTO -> Entity 변환
        ApplyEntity applyEntity = ApplyEntity.builder()

                //mid가 비어있으면 에러발생하여 임시로 넣습니다.
                .mid(applyDTO.getMid() != null ? applyDTO.getMid() : "test_user")

                .applicantName(applyDTO.getApplicantName())
                .phone(applyDTO.getPhone())
                .eventName(applyDTO.getEventName())
                .facilityType(applyDTO.getFacilityType())
                .participants(applyDTO.getParticipants())
                .applyDate(applyDTO.getApplyDate())
                .applyTime(applyDTO.getApplyTime())
                .eventContent((applyDTO.getEventContent() == null || applyDTO.getEventContent().trim().isEmpty())
                        ? "상세 내용 없음" : applyDTO.getEventContent())
                .inquiryContent((applyDTO.getInquiryContent() == null || applyDTO.getInquiryContent().trim().isEmpty())
                        ? "문의사항 없음" : applyDTO.getInquiryContent())
                .build();

        // DB 저장
        ApplyEntity result = applyRepository.save(applyEntity);

        return result.getAno(); // 저장된 번호 반환
    }

    @Override
    public List<ApplyDTO> getApplyListByMid(String mid) {
        log.info("내 신청 내역 조회 요청 - 회원 ID: " + mid);

        // 1. 레포지토리에서 해당 mid의 데이터 조회
        List<ApplyEntity> result = applyRepository.findByMid(mid);

        // 2. Entity 리스트 -> DTO 리스트 변환 (Stream 활용)
        return result.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApplyDTO getApply(Long ano) {
        // 1. DB에서 해당 번호의 엔티티를 찾음 (없으면 예외 발생)
        java.util.Optional<ApplyEntity> result = applyRepository.findById(ano);
        ApplyEntity entity = result.orElseThrow();

        // 2. 엔티티를 DTO로 변환해서 반환
        return entityToDto(entity);
    }
}

/*
 * ========== ApplyServiceImpl 설명 ==========
 * - 역할: ApplyService 인터페이스의 구현체. 대관 신청서 등록 처리
 * - 쓰이는 곳: ApplyController에서 주입받아 사용
 *
 * [register() 동작]
 * 1. ApplyDTO → ApplyEntity 변환 (Builder 패턴)
 * 2. mid가 null이면 임시값 "test_user" 세팅 (로그인 연동 전 임시 처리)
 * 3. eventContent/inquiryContent가 비어있으면 기본값 세팅
 * 4. DB 저장 후 생성된 ano(신청 번호) 반환
 */