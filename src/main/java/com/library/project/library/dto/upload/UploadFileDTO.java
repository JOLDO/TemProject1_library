package com.library.project.library.dto.upload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFileDTO {
    // 자료구조, 데이터 + 파일 , 첨부할수 있는 구조다.
    private List<MultipartFile> files;
}
