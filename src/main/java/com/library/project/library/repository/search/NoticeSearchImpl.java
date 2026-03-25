package com.library.project.library.repository.search;

import com.library.project.library.domain.Notice;
import com.library.project.library.domain.QNotice;
import com.library.project.library.dto.NoticeImageDTO;
import com.library.project.library.dto.NoticeListAllDTO;
import com.library.project.library.dto.NoticeListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NoticeSearchImpl extends QuerydslRepositorySupport implements NoticeSearch {

    public NoticeSearchImpl() {
        super(Notice.class);
    }

    // 1. search1 구현 (인터페이스에 있어서 반드시 필요)
    @Override
    public Page<Notice> search1(Pageable pageable) {
        QNotice notice = QNotice.notice;
        JPQLQuery<Notice> query = from(notice);
        this.getQuerydsl().applyPagination(pageable, query);
        List<Notice> list = query.fetch();
        long count = query.fetchCount();
        return new PageImpl<>(list, pageable, count);
    }

    // 2. searchAll 구현
    @Override
    public Page<Notice> searchAll(String[] types, String keyword, Pageable pageable) {
        QNotice notice = QNotice.notice;
        JPQLQuery<Notice> query = from(notice);

        if ((types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t": booleanBuilder.or(notice.title.contains(keyword)); break;
                    case "c": booleanBuilder.or(notice.content.contains(keyword)); break;
                    case "w": booleanBuilder.or(notice.writer.contains(keyword)); break;
                }
            }
            query.where(booleanBuilder);
        }
        query.where(notice.nno.gt(0L));
        this.getQuerydsl().applyPagination(pageable, query);
        List<Notice> list = query.fetch();
        long count = query.fetchCount();
        return new PageImpl<>(list, pageable, count);
    }

    // 3. searchWithReplyCount 구현 (빈 통이라도 있어야 오류가 안 납니다)
    @Override
    public Page<NoticeListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        // 현재는 댓글 기능을 안 쓰신다면 null이나 빈 페이지를 반환하도록 설정
        return null;
    }

    // 4. searchWithAll 구현 (이미지 포함 버전)
    @Override
    public Page<NoticeListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {
        QNotice notice = QNotice.notice;
        JPQLQuery<Notice> query = from(notice);

        if ((types != null && types.length > 0) && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for (String type : types) {
                switch (type) {
                    case "t": booleanBuilder.or(notice.title.contains(keyword)); break;
                    case "c": booleanBuilder.or(notice.content.contains(keyword)); break;
                    case "w": booleanBuilder.or(notice.writer.contains(keyword)); break;
                }
            }
            query.where(booleanBuilder);
        }

        this.getQuerydsl().applyPagination(pageable, query);
        List<Notice> list = query.fetch();

        List<NoticeListAllDTO> dtoList = list.stream().map(n -> {
            NoticeListAllDTO dto = NoticeListAllDTO.builder()
                    .nno(n.getNno())
                    .title(n.getTitle())
                    .writer(n.getWriter())
                    .regDate(n.getRegDate())
                    .replyCount(0L)
                    .build();

            List<NoticeImageDTO> imageDTOS = n.getImageSet().stream().sorted().map(img ->
                    NoticeImageDTO.builder()
                            .uuid(img.getUuid())
                            .fileName(img.getFileName())
                            .ord(img.getOrd())
                            .build()
            ).collect(Collectors.toList());

            dto.setNoticeImages(imageDTOS);
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, query.fetchCount());
    }
}