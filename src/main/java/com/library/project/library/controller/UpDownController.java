package com.library.project.library.controller;

import com.library.project.library.dto.upload.UploadFileDTO;
import com.library.project.library.dto.upload.UploadResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
public class UpDownController {

    @Value("${com.busanit501.upload.path}")
    private String uploadPath;

    @Tag(name = "이미지 파일 업로드 테스트",
            description = "post 방식으로 멀티파트 폼에 이미지를 첨부해서 서버에 전달하기.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
        log.info("UpDownController 이미지 첨부 테스트 확인");

        if (uploadFileDTO.getFiles() != null) {
            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(file -> {
                String originName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_" + originName);

                boolean image = false;

                try {
                    file.transferTo(savePath);

                    if (Files.probeContentType(savePath).startsWith("image")) {
                        image = true;
                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originName)
                        .img(image)
                        .build());
            });
            return list;
        }
        return null;
    }

    // [수정 포인트] FileController와의 충돌을 피하기 위해 주소를 /display 로 변경했습니다.
    @Tag(name = "이미지 파일 조회 테스트",
            description = "get 방식으로 이미지 확인하기.")
    @GetMapping(value = "/display/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();
        log.info("UpDownController resourceName : " + resourceName);

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Tag(name = "이미지 파일 삭제 테스트",
            description = "DELETE 방식으로 이미지 삭제 확인하기.")
    @DeleteMapping(value = "/remove/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            if (contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);
        return resultMap;
    }
}