package com.library.project.library.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    // 화면에서, 해당 이미지에 접근 하기 위해서는
    // 예시 , 쎔네일 이미지의 파일명
    // 예시 : s_5b418a60-407e-406e-991e-db88d35ea426_크롬기준-로컬스토리지 저장소 확인 방법.PNG
    private String uuid; // 5b418a60-407e-406e-991e-db88d35ea426
    private String fileName; // 크롬기준-로컬스토리지 저장소 확인 방법.PNG
    private boolean img; // 이미지 여부 확인.

    public String getLink() {
        if(img){
            return "s_" + uuid + "_" + fileName;
        } else {
            return uuid + "_" + fileName;
        }
    }
}
