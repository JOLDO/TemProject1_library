package com.library.project.library.repository.search;


import com.library.project.library.domain.Inquiry;
import com.library.project.library.domain.QInquiry;
import com.library.project.library.domain.QReply;
import com.library.project.library.dto.InquiryListReplyCountDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class InquirySearchImpl extends QuerydslRepositorySupport implements InquirySearch {

    public InquirySearchImpl() {
        super(Inquiry.class);
    }

    @Override
    public Page<Inquiry> searchAll(String[] types, String keyword, Pageable pageable) {
        QInquiry inquiry = QInquiry.inquiry;
        JPQLQuery<Inquiry> query = from(inquiry);
        if(types != null && types.length > 0 && keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            for(String type : types) {
                switch (type){
                    case "t": booleanBuilder.or(inquiry.title.contains(keyword)); break;
                    case "c": booleanBuilder.or(inquiry.content.contains(keyword)); break;
                    case "w": booleanBuilder.or(inquiry.writer.contains(keyword)); break;
                }
            }
            query.where(booleanBuilder);
        }
        query.where(inquiry.ino.gt(0L));
        this.getQuerydsl().applyPagination(pageable, query);
        List<Inquiry> list = query.fetch();
        long total = query.fetchCount();
        return new PageImpl<>(list, pageable, total);
    }

    // 💡 기존 메서드: 전체 목록용 (writer를 null로 보냄)
    @Override
    public Page<InquiryListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        return performSearch(types, keyword, pageable, null);
    }


    // 💡 새로 추가된 메서드: 내 글 목록용 (writer 아이디를 보냄)
    @Override
    public Page<InquiryListReplyCountDTO> searchMyList(Pageable pageable, String writer) {
        return performSearch(null, null, pageable, writer);
    }


    // 💡 중복을 제거한 공통 검색 로직
    private Page<InquiryListReplyCountDTO> performSearch(String[] types, String keyword, Pageable pageable, String writer) {
        QInquiry inquiry = QInquiry.inquiry;
        QReply reply = QReply.reply;

        JPQLQuery<Inquiry> query = from(inquiry);
        query.leftJoin(reply).on(reply.inquiry.eq(inquiry));
        query.groupBy(inquiry);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 검색어가 있을 때 (list용)
        if(types != null && types.length > 0 && keyword != null) {
            for(String type : types) {
                switch (type){
                    case "t": booleanBuilder.or(inquiry.title.contains(keyword)); break;
                    case "c": booleanBuilder.or(inquiry.content.contains(keyword)); break;
                    case "w": booleanBuilder.or(inquiry.writer.contains(keyword)); break;
                }
            }
            query.where(booleanBuilder);
        }

        // 💡 작성자 조건 추가 (myList용)
        if(writer != null) {
            query.where(inquiry.writer.eq(writer));
        }

        query.where(inquiry.ino.gt(0L));

        JPQLQuery<InquiryListReplyCountDTO> dtoQuery = query.select(Projections.bean(InquiryListReplyCountDTO.class,
                inquiry.ino,
                inquiry.title,
                inquiry.writer,
                inquiry.regDate,
                inquiry.secret,
                reply.count().as("replyCount")
        ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);

        List<InquiryListReplyCountDTO> dtoList = dtoQuery.fetch();
        long total = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }
}