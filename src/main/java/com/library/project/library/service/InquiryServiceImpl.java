package com.library.project.library.service;

import com.library.project.library.domain.Inquiry;
import com.library.project.library.dto.InquiryDTO;
import com.library.project.library.dto.InquiryListReplyCountDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.entity.Member;
import com.library.project.library.repository.InquiryRepository;
import com.library.project.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 📍 영속성 유지를 위해 클래스 레벨에 반드시 필요
public class InquiryServiceImpl implements InquiryService {

    private final ModelMapper modelMapper;
    private final InquiryRepository inquiryRepository;
    private final MemberRepository memberRepository;

    // 1. 문의사항 등록
    @Override
    public Long register(InquiryDTO inquiryDTO) {

        log.info(">>>> [서비스] 문의사항 등록 시도 - 전달된 mid: [{}]", inquiryDTO.getMid());

        // 📍 1단계: mid가 전달되었는지 확인 (null 체크)
        if (inquiryDTO.getMid() == null || inquiryDTO.getMid().trim().isEmpty()) {
            log.error(">>>> [에러] 작성자 아이디(mid)가 누락되었습니다!");
            throw new IllegalArgumentException("작성자 아이디(mid)가 누락되었습니다. 로그인을 확인하세요.");
        }

        // 📍 2단계: DB에서 영속 상태의 Member를 확실히 가져옵니다.
        // 이 member 객체는 JPA가 관리하는 상태(Managed)이므로 Transient 에러를 방지합니다.
        Member member = memberRepository.findByMid(inquiryDTO.getMid())
                .orElseThrow(() -> {
                    log.error(">>>> [에러] DB에 해당 회원이 없습니다. 입력된 mid: {}", inquiryDTO.getMid());
                    return new IllegalArgumentException("DB에 존재하지 않는 회원입니다. mid: " + inquiryDTO.getMid());
                });

        log.info(">>>> [서비스] DB 조회 성공! 회원 고유 PK(id): {}", member.getId());

        // 📍 3단계: 빌더를 통해 Inquiry 생성 및 저장
        Inquiry inquiry = Inquiry.builder()
                .title(inquiryDTO.getTitle())
                .content(inquiryDTO.getContent())
                .member(member) // DB에서 가져온 영속 객체 주입
                .secret(inquiryDTO.isSecret())
                .answered(false)
                .build();

        log.info(">>>> [서비스] Inquiry 엔티티 저장 실행...");
        return inquiryRepository.save(inquiry).getIno();
    }

    // 2. 상세 조회
    @Override
    public InquiryDTO readOne(Long ino) {
        Optional<Inquiry> result = inquiryRepository.findById(ino);
        Inquiry inquiry = result.orElseThrow(() -> new NoSuchElementException("해당 문의글이 존재하지 않습니다."));

        InquiryDTO inquiryDTO = modelMapper.map(inquiry, InquiryDTO.class);

        if(inquiry.getMember() != null) {
            inquiryDTO.setMid(inquiry.getMember().getMid());
        }

        return inquiryDTO;
    }

    // 3. 수정 처리
    @Override
    public void modify(InquiryDTO inquiryDTO) {
        Optional<Inquiry> result = inquiryRepository.findById(inquiryDTO.getIno());
        Inquiry inquiry = result.orElseThrow(() -> new NoSuchElementException("수정할 게시글이 없습니다."));

        inquiry.change(
                inquiryDTO.getTitle(),
                inquiryDTO.getContent(),
                inquiryDTO.isSecret()
        );

        inquiryRepository.save(inquiry);
    }

    // 4. 삭제 처리
    @Override
    public void remove(Long ino) {
        inquiryRepository.deleteById(ino);
    }

    // 5. 전체 목록 조회 (기본)
    @Override
    public PageResponseDTO<InquiryDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("ino");
        Page<Inquiry> result = inquiryRepository.searchAll(pageRequestDTO.getTypes(), pageRequestDTO.getKeyword(), pageable);

        List<InquiryDTO> dtoList = result.getContent().stream().map(inquiry -> {
            InquiryDTO dto = modelMapper.map(inquiry, InquiryDTO.class);
            if(inquiry.getMember() != null) {
                dto.setMid(inquiry.getMember().getMid());
            }
            return dto;
        }).collect(Collectors.toList());

        return PageResponseDTO.<InquiryDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    // 6. 전체 목록 조회 (댓글 개수 포함)
    @Override
    public PageResponseDTO<InquiryListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable("ino");
        Page<InquiryListReplyCountDTO> result = inquiryRepository.searchWithReplyCount(pageRequestDTO.getTypes(), pageRequestDTO.getKeyword(), pageable);

        return PageResponseDTO.<InquiryListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }

    // 7. 나의 문의 내역 조회
    @Override
    public PageResponseDTO<InquiryListReplyCountDTO> getMyInquiryList(String mid, PageRequestDTO pageRequestDTO) {
        log.info(">>>> [서비스] 나의 문의 내역 조회 아이디: " + mid);

        Pageable pageable = pageRequestDTO.getPageable("ino");
        Page<InquiryListReplyCountDTO> result = inquiryRepository.searchMyList(pageable, mid);

        return PageResponseDTO.<InquiryListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}