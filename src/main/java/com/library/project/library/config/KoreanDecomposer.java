package com.library.project.library.config;

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