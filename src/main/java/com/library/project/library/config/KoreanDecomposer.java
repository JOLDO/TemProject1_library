package com.library.project.library.config;

import org.springframework.stereotype.Component;

@Component
public class KoreanDecomposer {

    private static final char[] CHOSUNG = {
        'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    };

    public String toChosung(String text) {
        if (text == null) return null;

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= 0xAC00 && c <= 0xD7A3) {
                // 한글 음절인 경우: 초성만 추출
                // (c - 0xAC00): 0xAC00 기준 음절 인덱스 계산
                // / (21 * 28) : 중성(21)×종성(28) = 588가지를 나누면 초성 인덱스가 나옴
                int syllable = c - 0xAC00;
                sb.append(CHOSUNG[syllable / (21 * 28)]);
            } else if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public String toNormal(String text) {
        if (text == null) return null;

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (Character.isLetterOrDigit(c) || (c >= 0xAC00 && c <= 0xD7A3)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}

/*
 * ========== KoreanDecomposer 설명 ==========
 * - 역할: 한글 검색 지원을 위한 문자열 변환 유틸 클래스
 * - 쓰이는 곳: BookServiceImpl.list()에서 검색어 변환, Book 엔티티 저장 시 제목 변환
 * - Bean 등록: RootConfig에서 @Bean으로 등록
 *
 * [메서드]
 * - toChosung(): 한글 문자열에서 초성만 추출 (예: "스프링" → "ㅅㅍㄹ")
 *   → 초성 검색 기능에 사용 (bookTitleChosung 필드)
 *
 * - toNormal(): 한글+영문+숫자만 남기고 특수문자 제거 (예: "[스프링!]" → "스프링")
 *   → 정규화 검색 기능에 사용 (bookTitleNormal 필드)
 *
 * [한글 초성 추출 원리]
 * - 유니코드 한글 음절 범위: 0xAC00 ~ 0xD7A3
 * - (음절코드 - 0xAC00) / (21 * 28) = 초성 인덱스
 */