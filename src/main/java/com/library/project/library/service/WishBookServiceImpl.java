package com.library.project.library.service;

import com.library.project.library.dto.WishBookDTO;
import com.library.project.library.entity.WishBookEntity; // 엔티티 경로 확인 필요
import com.library.project.library.repository.WishBookRepository; // 리포지토리 경로 확인 필요
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 서비스단에는 트랜잭션 처리를 해주는 것이 안전합니다.
public class WishBookServiceImpl implements WishBookService {

    private final WishBookRepository wishBookRepository;

    @Override
    public Long register(WishBookDTO wishBookDTO) {

        log.info("Service: DB 저장을 위한 데이터 변환 중...");

        WishBookEntity entity = WishBookEntity.builder()
                // 1. 필수 값 세팅 (가장 중요!)
                .mid(wishBookDTO.getMid() != null ? wishBookDTO.getMid() : "user01") // 임시 ID 혹은 세션 ID
                .status("신청중") // 기본 상태값 설정

                // 2. 신청 데이터 (기존 코드)
                .applicantName(wishBookDTO.getWishApplicantName())
                .wishPhone(wishBookDTO.getWishPhone())
                .wishBookTitle(wishBookDTO.getWishBookTitle())
                .wishAuthor(wishBookDTO.getWishAuthor())
                .wishPublisher(wishBookDTO.getWishPublisher())

                // 3. 파일명 (있다면 세팅)
                .fileName(wishBookDTO.getWishBookImage() != null ?
                        wishBookDTO.getWishBookImage().getOriginalFilename() : null)
                .build();

        // 4. 저장 실행
        WishBookEntity result = wishBookRepository.save(entity);

        log.info("Service: DB 저장 성공! 생성된 번호: " + result.getWno());

        return result.getWno();
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