package com.library.project.library.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<byte[]> handleRuntimeException(RuntimeException e) throws Exception {
        byte[] body = e.getMessage().getBytes("UTF-8");
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Type", "text/plain;charset=UTF-8");
        return new ResponseEntity<>(body, headers, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    // NotLoginException 발생 시 401 응답 반환
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<String> handleNotLogin(NotLoginException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}

/*
 * ========== GlobalExceptionHandler 설명 ==========
 * - 역할: REST API에서 발생하는 예외를 잡아서 적절한 HTTP 응답으로 변환하는 @RestControllerAdvice
 * - 쓰이는 곳: 전역 예외 처리 (모든 컨트롤러에 자동 적용)
 *
 * [메서드]
 * - handleRuntimeException(): RuntimeException 발생 시 400(BAD_REQUEST) 응답 + UTF-8 메시지 반환
 * - handleNotLogin(): NotLoginException 발생 시 401(UNAUTHORIZED) 응답 반환
 */