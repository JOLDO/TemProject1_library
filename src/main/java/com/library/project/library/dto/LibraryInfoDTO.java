package com.library.project.library.dto;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryInfoDTO {

    private Long id; // info_id 매핑용
    private String libraryName;
    private String address;
    private String contact;
    private String donationGuide;

    // Lombok @Builder가 자동으로 아래와 같은 builder() 메서드를 생성해줍니다.
}