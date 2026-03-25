package com.library.project.library.service;

import com.library.project.library.domain.Notice;
import com.library.project.library.dto.NoticeDTO;
import com.library.project.library.dto.NoticeListAllDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final ModelMapper modelMapper;
    private final NoticeRepository noticeRepository;

    @Override
    public Long register(NoticeDTO noticeDTO) {
        // DTO -> Entity 변환 (인터페이스의 default 메서드 활용)
        Notice notice = dtoToEntity(noticeDTO);
        Long nno = noticeRepository.save(notice).getNno();
        return nno;
    }

    @Override
    public NoticeDTO readOne(Long nno) {
        // 게시글 번호로 조회 (이미지 셋까지 함께 가져오기 위해 findById 활용)
        Optional<Notice> result = noticeRepository.findById(nno);
        Notice notice = result.orElseThrow();

        // Entity -> DTO 변환 (인터페이스의 default 메서드 활용)
        return entityToDto(notice);
    }

    @Override
    public void modify(NoticeDTO noticeDTO) {
        Optional<Notice> result = noticeRepository.findById(noticeDTO.getNno());
        Notice notice = result.orElseThrow();

        // 제목과 내용 수정
        notice.change(noticeDTO.getTitle(), noticeDTO.getContent());

        // 기존 첨부파일 삭제 후 새로 추가 (이미지 수정 로직)
        notice.clearImages();
        if(noticeDTO.getFileNames() != null){
            for (String fileName : noticeDTO.getFileNames()) {
                String[] arr = fileName.split("_", 2);
                notice.addImage(arr[0], arr[1]);
            }
        }
        noticeRepository.save(notice);
    }

    @Override
    public void remove(Long nno) {
        noticeRepository.deleteById(nno);
    }


    // 1. 일반 페이징 목록 (검색 포함)
    @Override
    public PageResponseDTO<NoticeDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("nno"); // nno 기준 정렬

        // Querydsl을 이용한 검색 결과 가져오기
        Page<Notice> result = noticeRepository.searchAll(types, keyword, pageable);

        // Entity 리스트를 DTO 리스트로 변환
        java.util.List<NoticeDTO> dtoList = result.getContent().stream()
                .map(this::entityToDto)
                .collect(java.util.stream.Collectors.toList());

        return PageResponseDTO.<NoticeDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    // 2. 전체 목록 + 이미지 + 댓글 개수 포함 (NoticeListAllDTO 활용)
    @Override
    public PageResponseDTO<NoticeListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("nno");

        // QuerydslRepositorySupport를 상속받은 repository의 searchWithAll 호출
        Page<NoticeListAllDTO> result = noticeRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<NoticeListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }
}