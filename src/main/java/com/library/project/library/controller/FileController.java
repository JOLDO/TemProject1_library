package com.library.project.library.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    @Value("${com.busanit501.upload.path}") // application.properties에 적은 C:\\upload 가져오기
    private String uploadPath;

    @GetMapping("/display")
    public ResponseEntity<Resource> display(@RequestParam("fileName") String fileName) {

        // 1. 실제 파일 경로 합치기 (C:\\upload\\ + movie/movie1.webp)
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        if(!resource.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            // 2. 파일의 확장자(jpg, webp 등)를 확인해서 헤더에 담기
            Path filePath = Paths.get(uploadPath + File.separator + fileName);
            headers.add("Content-Type", Files.probeContentType(filePath));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 3. 사진 데이터 전송!
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}

/*
 * ========== FileController 설명 ==========
 * - 역할: 업로드된 파일(이미지)을 브라우저에 전달하는 REST 컨트롤러
 * - URL 패턴: /display?fileName=xxx
 *
 * [display() 동작]
 * 1. fileName 파라미터로 파일 경로 수신
 * 2. uploadPath(C:/upload) + fileName으로 실제 파일 위치 조합
 * 3. 파일 존재 여부 확인 (없으면 404)
 * 4. 파일 확장자로 Content-Type 헤더 설정 (jpg, webp 등)
 * 5. 파일 데이터를 ResponseEntity로 반환
 */
