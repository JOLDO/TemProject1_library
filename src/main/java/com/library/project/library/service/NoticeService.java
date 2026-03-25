package com.library.project.library.service;

import com.library.project.library.domain.Notice;
import com.library.project.library.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public interface NoticeService {

    Long register(NoticeDTO noticeDTO);
    NoticeDTO readOne(Long nno);
    void modify(NoticeDTO noticeDTO);
    void remove(Long nno);

    // 기본 페이징 목록
    PageResponseDTO<NoticeDTO> list(PageRequestDTO pageRequestDTO);

    // 전체 목록 + 이미지 + 댓글 개수 포함 (리스트 화면용)
    PageResponseDTO<NoticeListAllDTO> listWithAll(PageRequestDTO pageRequestDTO);

    /**
     * DTO -> Entity 변환 (게시글 등록/수정 시 사용)
     */
    default Notice dtoToEntity(NoticeDTO dto) {
        Notice notice = Notice.builder()
                .nno(dto.getNno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        // 첨 be 이미지 처리
        if(dto.getFileNames() != null) {
            dto.getFileNames().forEach(fileName -> {
                // 📍 수정: split("_", 2)를 사용하여 파일명에 '_'가 있어도 안전하게 분리
                String[] arr = fileName.split("_", 2);
                notice.addImage(arr[0], arr[1]);
            });
        }
        return notice;
    }

    /**
     * Entity -> DTO 변환 (상세 조회 시 사용)
     */
    default NoticeDTO entityToDto(Notice notice) {
        NoticeDTO noticeDTO = NoticeDTO.builder()
                .nno(notice.getNno())
                .title(notice.getTitle())
                .content(notice.getContent()) // 📍 수정: 기존 title이 들어가던 오타 수정
                .writer(notice.getWriter())
                .regDate(notice.getRegDate())
                .modDate(notice.getModDate())
                .build();

        // 첨부 이미지 리스트를 "UUID_파일명" 형태의 문자열 리스트로 변환
        if(notice.getImageSet() != null) {
            List<String> fileNames = notice.getImageSet().stream()
                    .sorted() // ord 순서대로 정렬
                    .map(noticeImage -> noticeImage.getUuid() + "_" + noticeImage.getFileName())
                    .collect(Collectors.toList());

            noticeDTO.setFileNames(fileNames);
        }

        return noticeDTO;
    }

}