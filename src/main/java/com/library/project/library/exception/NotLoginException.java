package com.library.project.library.exception;

// 로그인이 필요한 기능에 비로그인 사용자가 접근했을 때 발생하는 예외
public class NotLoginException extends RuntimeException {
    public NotLoginException(String message) {
        super(message);
    }
    public NotLoginException() {
        super("로그인이 필요합니다.");
    }
}