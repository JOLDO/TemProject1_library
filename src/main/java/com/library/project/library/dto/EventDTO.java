package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private Long id;
    private String category;
    private String title;
    private String content;
    private LocalDateTime eventDate;
    private String place;
    private String extraInfo;
    private String imageName;
    private String status;
}