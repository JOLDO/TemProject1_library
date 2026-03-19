package com.library.project.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "library_info")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LibraryInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_id") // image_c8705e.png 확인: PK 컬럼명은 info_id입니다.
    private Long id;

    @Column(name = "library_name", length = 255)
    private String libraryName;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "contact", length = 50)
    private String contact;

    @Column(name = "donation_guide", columnDefinition = "TEXT")
    private String donationGuide;
}