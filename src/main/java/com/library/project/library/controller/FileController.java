package com.library.project.library.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;

@RestController
@Log4j2
public class FileController {

    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {

        // 📍 1. 서버가 실제로 어디를 뒤지는지 콘솔(Console)에 찍습니다.
        String finalPath = uploadPath + File.separator + fileName;
        log.info("---------------------------------------");
        log.info("📍 [FileController] 찾는 파일 경로: " + finalPath);

        Resource resource = new FileSystemResource(finalPath);

        // 📍 2. 파일이 진짜 그 자리에 있는지 확인합니다.
        if(!resource.exists()) {
            log.error("❌ 에러: 파일이 해당 경로에 존재하지 않습니다!");
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            log.error("❌ 에러: 파일 타입을 읽을 수 없습니다.");
            return ResponseEntity.internalServerError().build();
        }

        log.info("✅ 성공: 파일을 브라우저로 전송합니다.");
        log.info("---------------------------------------");
        return ResponseEntity.ok().headers(headers).body(resource);
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
