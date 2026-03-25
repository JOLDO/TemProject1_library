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
                    // 📍 [수정] writer 대신 member.mid를 검색합니다.
                    case "w": booleanBuilder.or(inquiry.member.mid.contains(keyword)); break;
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

    @Override
    public Page<InquiryListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {
        return performSearch(types, keyword, pageable, null);
    }

    @Override
    public Page<InquiryListReplyCountDTO> searchMyList(Pageable pageable, String mid) {
        // 📍 매개변수 이름을 mid로 변경하여 가독성을 높였습니다.
        return performSearch(null, null, pageable, mid);
    }

    private Page<InquiryListReplyCountDTO> performSearch(String[] types, String keyword, Pageable pageable, String mid) {
        QInquiry inquiry = QInquiry.inquiry;
        QReply reply = QReply.reply;

        JPQLQuery<Inquiry> query = from(inquiry);
        query.leftJoin(reply).on(reply.inquiry.eq(inquiry));
        query.groupBy(inquiry);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(types != null && types.length > 0 && keyword != null) {
            for(String type : types) {
                switch (type){
                    case "t": booleanBuilder.or(inquiry.title.contains(keyword)); break;
                    case "c": booleanBuilder.or(inquiry.content.contains(keyword)); break;
                    // 📍 [수정] 검색 시 member.mid를 바라보게 합니다.
                    case "w": booleanBuilder.or(inquiry.member.mid.contains(keyword)); break;
                }
            }
            query.where(booleanBuilder);
        }

        // 📍 [수정] myList 조회 시에도 member.mid와 비교합니다.
        if(mid != null) {
            query.where(inquiry.member.mid.eq(mid));
        }

        query.where(inquiry.ino.gt(0L));

        JPQLQuery<InquiryListReplyCountDTO> dtoQuery = query.select(Projections.bean(InquiryListReplyCountDTO.class,
                inquiry.ino,
                inquiry.title,
                // 📍 [중요] DTO에 값을 담을 때도 mid를 가져와야 합니다.
                // InquiryListReplyCountDTO에 mid 필드가 있어야 작동합니다.
                inquiry.member.mid.as("mid"),
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