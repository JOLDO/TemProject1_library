package com.library.project.library.service;

import com.library.project.library.dto.WishBookDTO;
import com.library.project.library.entity.WishBookEntity; // 엔티티 경로 확인 필요
import com.library.project.library.repository.WishBookRepository; // 리포지토리 경로 확인 필요
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class WishBookServiceImpl implements WishBookService {

    private final WishBookRepository wishBookRepository;

    @Override
    public Long register(WishBookDTO wishBookDTO) {
        log.info("Service: DB 저장을 위한 데이터 변환 중...");

        WishBookEntity entity = WishBookEntity.builder()
                .mid(wishBookDTO.getMid()) // 컨트롤러에서 세션 ID를 넘겨주므로 그대로 사용
                .status("신청중")
                .applicantName(wishBookDTO.getWishApplicantName())
                .wishPhone(wishBookDTO.getWishPhone())
                .wishBookTitle(wishBookDTO.getWishBookTitle())
                .wishAuthor(wishBookDTO.getWishAuthor())
                .wishPublisher(wishBookDTO.getWishPublisher())
                .fileName(wishBookDTO.getWishBookImage() != null && !wishBookDTO.getWishBookImage().isEmpty() ?
                        wishBookDTO.getWishBookImage().getOriginalFilename() : null)
                .build();

        WishBookEntity result = wishBookRepository.save(entity);
        log.info("Service: DB 저장 성공! 생성된 번호: " + result.getWno());

        return result.getWno();
    }

    // [추가] 내 신청 내역 조회 로직
    @Override
    public List<WishBookDTO> getList(String mid) {
        log.info("Service: " + mid + " 사용자의 신청 내역 조회 중...");

        // 1. Repository에서 mid로 엔티티 리스트 조회 (정렬: 최근순)
        List<WishBookEntity> result = wishBookRepository.findByMidOrderByWnoDesc(mid);

        // 2. Entity 리스트를 DTO 리스트로 변환 (Stream 활용)
        return result.stream().map(entity -> WishBookDTO.builder()
                .wno(entity.getWno())
                .wishApplicantName(entity.getApplicantName())
                .wishBookTitle(entity.getWishBookTitle())
                .wishAuthor(entity.getWishAuthor())
                .wishPublisher(entity.getWishPublisher())
                .status(entity.getStatus()) // Entity에 status 필드가 있어야 합니다.
                .regDate(entity.getRegDate()) // 등록일자
                .build()
        ).collect(Collectors.toList());
    }
}

/*
 * ========== WishBookServiceImpl 설명 ==========
 * - 역할: WishBookService 인터페이스의 구현체. 희망도서 신청 등록 처리
 * - 쓰이는 곳: WishBookController에서 주입받아 사용
 *
 * [register() 동작]
 * 1. WishBookDTO → WishBookEntity 변환 (Builder 패턴)
 * 2. mid가 null이면 임시값 "user01" 세팅 (로그인 연동 전 임시 처리)
 * 3. status 기본값 "신청중" 세팅
 * 4. 이미지 파일이 있으면 원본 파일명 저장
 * 5. DB 저장 후 생성된 wno 반환
 */