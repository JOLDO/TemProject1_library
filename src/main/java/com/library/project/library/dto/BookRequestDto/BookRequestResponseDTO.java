package com.library.project.library.dto.BookRequestDto;

import com.library.project.library.entity.BookRequest;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class BookRequestResponseDTO {

    private Long requestId;
    private Long memberId;
    private String memberName;  // 회원 이름
    private Long bookId;
    private String bookTitle;   // 도서명
    private String bookAuthor;  // 저자
    private LocalDate requestDate;
    private String status;      // PENDING, APPROVED, REJECTED

    public static BookRequestResponseDTO from(BookRequest r) {
        return BookRequestResponseDTO.builder()
                .requestId(r.getId())
                .memberId(r.getMember().getId())
                .memberName(r.getMember().getMname())
                .bookId(r.getBook().getId())
                .bookTitle(r.getBook().getBookTitle())
                .bookAuthor(r.getBook().getAuthor())
                .requestDate(r.getRequestDate())
                .status(r.getStatus().name())
                .build();
    }
}
