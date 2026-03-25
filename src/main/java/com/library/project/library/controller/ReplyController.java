package com.library.project.library.controller;


import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.dto.ReplyDTO;
import com.library.project.library.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/replies")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/")
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody ReplyDTO replyDTO) {
        Long rno = replyService.register(replyDTO);
        return ResponseEntity.ok(Map.of("rno", rno));
    }

    @GetMapping("/list/{ino}")
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("ino") Long ino, PageRequestDTO pageRequestDTO) {
        return replyService.getListOfInquiry(ino, pageRequestDTO);
    }

    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno) {
        replyService.remove(rno);
        return Map.of("rno", rno);
    }
}