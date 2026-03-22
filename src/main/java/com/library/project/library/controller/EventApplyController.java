package com.library.project.library.controller;

import com.library.project.library.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventApplyController {

    private final EventService eventService;

    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody Map<String, Long> data, Principal principal) {

        // 1. 로그인 체크 (한 번 더 검증)
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long eventId = data.get("eventId");
        String mid = principal.getName(); // 현재 로그인한 아이디(mid)

        try {
            // 2. 서비스 호출 (신청 로직 실행)
            eventService.applyEvent(eventId, mid);
            return ResponseEntity.ok("Success");
        } catch (IllegalStateException e) {
            // 중복 신청 시 예외 처리
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}

/*
 * ========== EventApplyController 설명 ==========
 * - 역할: 행사 신청 처리 REST API 컨트롤러
 * - URL 패턴: /event/**
 *
 * [메서드]
 * - apply(): POST /event/apply → 행사 신청 처리 (로그인 검증 + 중복 신청 방지)
 */