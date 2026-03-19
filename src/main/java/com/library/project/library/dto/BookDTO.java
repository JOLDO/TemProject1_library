package com.library.project.library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.library.project.library.enums.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;
    private String isbn;
    private String bookTitle;
    private String bookImage;
    private String author;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pubdate;
    private String description;
    private String bookTitleNormal;
    private String bookTitleChosung;
    private BookStatus status;
    private boolean recommended;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime modDate;
}