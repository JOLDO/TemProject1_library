package com.library.project.library.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PageResponseDTO<E> {

    private int page;
    private int size;
    private int total;

    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {

        if(total <= 0) {
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.end = (int)(Math.ceil(this.page/10.0)) * 10;
        this.start = this.end - 9;
        int last = (int)(Math.ceil(total/(double)size));
        this.end = end > last ? last : end;
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }
}

/*
 * ========== PageResponseDTO 설명 ==========
 * - 역할: 페이징 결과를 화면에 전달하는 제네릭 DTO (목록 데이터 + 페이지 정보)
 * - 쓰이는 곳: BookServiceImpl에서 생성 → BookController/BookRestController에서 뷰에 전달
 *
 * [주요 필드]
 * - page / size: 현재 페이지 / 페이지당 항목 수
 * - total: 전체 데이터 수
 * - start / end: 페이지 번호 범위 (10개씩 묶음. 예: 1~10, 11~20)
 * - prev / next: 이전/다음 페이지 그룹 존재 여부
 * - dtoList: 현재 페이지의 데이터 목록
 *
 * [생성 방식]
 * - PageResponseDTO.withAll() 빌더로 생성
 * - 생성자에서 start/end/prev/next를 자동 계산
 */
