package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String keyword;
    private String sort;
    private String link;

    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending() );
    }

    public String getLink() {
        if(link == null || link.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(keyword != null){
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if(sort != null){
                builder.append("&sort=" + sort);
            }
            link = builder.toString();
        }
        return link;
    }
}

/*
 * ========== PageRequestDTO 설명 ==========
 * - 역할: 페이징 요청 정보를 담는 DTO (페이지 번호, 크기, 검색어, 정렬 기준)
 * - 쓰이는 곳: BookController, BookRestController, BookServiceImpl 등 페이징이 필요한 곳에서 사용
 *
 * [주요 필드]
 * - page: 현재 페이지 번호 (기본값 1)
 * - size: 페이지당 항목 수 (기본값 10)
 * - keyword: 검색어
 * - sort: 정렬 기준 (id / pubdate / bookTitle / recommend / rental)
 * - link: 페이지 링크 문자열 (캐싱)
 *
 * [메서드]
 * - getPageable(): Spring Data의 Pageable 객체 생성 (page는 0-based로 변환)
 * - getLink(): 쿼리스트링 생성 (page, size, keyword, sort 포함). 한번 생성 후 캐싱
 */
