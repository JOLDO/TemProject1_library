package com.library.project.library.exception;


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
}