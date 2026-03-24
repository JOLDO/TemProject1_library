package com.library.project.library.service;

import com.library.project.library.domain.Inquiry;
import com.library.project.library.dto.InquiryDTO;
import com.library.project.library.dto.InquiryListReplyCountDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

    private final ModelMapper modelMapper;
    private final InquiryRepository inquiryRepository;

    @Override
    public Long register(InquiryDTO inquiryDTO) {
        Inquiry inquiry = modelMapper.map(inquiryDTO, Inquiry.class);
        log.info("문의사항 등록: " + inquiry);
        return inquiryRepository.save(inquiry).getIno();
    }

    @Override
    public InquiryDTO readOne(Long ino) {
        Optional<Inquiry> result = inquiryRepository.findById(ino);
        Inquiry inquiry = result.orElseThrow();
        return modelMapper.map(inquiry, InquiryDTO.class);
    }

    @Override
    public void modify(InquiryDTO inquiryDTO) {
        Optional<Inquiry> result = inquiryRepository.findById(inquiryDTO.getIno());
        Inquiry inquiry = result.orElseThrow();

        // 엔티티의 change 메서드 (제목, 내용, 비밀글여부 수정)
        inquiry.change(
                inquiryDTO.getTitle(),
                inquiryDTO.getContent(),
                inquiryDTO.isSecret()
        );

        inquiryRepository.save(inquiry);
    }

    @Override
    public void remove(Long ino) {
        inquiryRepository.deleteById(ino);
    }

    // 1. 기본 목록 조회
    @Override
    public PageResponseDTO<InquiryDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("ino");

        Page<Inquiry> result = inquiryRepository.searchAll(types, keyword, pageable);

        List<InquiryDTO> dtoList = result.getContent().stream()
                .map(inquiry -> modelMapper.map(inquiry, InquiryDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<InquiryDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    // 2. 답변(댓글) 개수 포함 전체 목록 조회
    @Override
    public PageResponseDTO<InquiryListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("ino");

        Page<InquiryListReplyCountDTO> result = inquiryRepository.searchWithReplyCount(types, keyword, pageable);

        return PageResponseDTO.<InquiryListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
    }


    // 3. 나의 문의 내역 조회 (댓글 개수 포함 버전)
    @Override
    public PageResponseDTO<InquiryListReplyCountDTO> getMyInquiryList(String mid, PageRequestDTO pageRequestDTO) {

        // 페이징 정보 생성 (기본적으로 글 번호 'ino' 역순 정렬)
        Pageable pageable = pageRequestDTO.getPageable("ino");

        // Querydsl 레포지토리의 searchMyList 호출 (아이디 필터링)
        Page<InquiryListReplyCountDTO> result = inquiryRepository.searchMyList(pageable, mid);

        // 결과를 PageResponseDTO 형태로 변환해서 반환
        return PageResponseDTO.<InquiryListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}