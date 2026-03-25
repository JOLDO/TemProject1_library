package com.library.project.library.repository.search;


import com.library.project.library.domain.Notice;
import com.library.project.library.dto.NoticeListAllDTO;
import com.library.project.library.dto.NoticeListReplyCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeSearch {
    // 검색의 결과도 , 페이징 처리를 할 예정,
    Page<Notice> search1(Pageable pageable);

    // 검색어(제목, 내용) , 페이징 처리 적용하는 메소드,
    Page<Notice> searchAll(String[] types, String keyword, Pageable pageable);

    // 페이징 + 검색 + 댓글의 갯수까지 포함할 예정.
    Page<NoticeListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

    // N+1 테스트
    // Page<BoardListReplyCountDTO> searchWithAll(String[] types, String keyword, Pageable pageable);

    //페이징 + 검색 + 댓글의 갯수 + 첨부 이미지까지 포함할 예정.
    Page<NoticeListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable);
}
