package com.library.project.library.dto.BookRequestDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder           // ← 추가
@NoArgsConstructor // ← 추가
@AllArgsConstructor // ← 추가
public class BookRequestDTO {
    private Long memberId;
    private Long bookId;
}
