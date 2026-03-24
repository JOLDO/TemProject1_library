package com.library.project.library.repository.search;


import com.library.project.library.domain.Inquiry;
import com.library.project.library.dto.InquiryListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface InquirySearch {
    // 기존 전체 목록
    Page<Inquiry> searchAll(String[] types, String keyword, Pageable pageable);

    // 댓글 개수 포함 리스트
    Page<InquiryListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

    // 📍 추가: 내 글 목록 (매개변수 순서를 Impl과 맞추세요)
    Page<InquiryListReplyCountDTO> searchMyList(Pageable pageable, String writer);
}