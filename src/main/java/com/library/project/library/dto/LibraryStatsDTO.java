package com.library.project.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryStatsDTO {
    private Long statId;
    private String categoryName;
    private int itemCount;
    private String lastUpdated;
    private String remarks;
    private Long infoId;
}
