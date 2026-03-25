package com.library.project.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 메인 페이지 및 공통 기능을 담당하는 컨트롤러입니다
 * 추가 유지보수 필요 건 발생 시 담당 팀원[이진주]에게 문의주세요🐾
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${weather.api.key}")
    private String weatherKey;

    @Value("${weather.api.url}")
    private String weatherUrl;

    @GetMapping("/")
    public String index(Model model) {
        // 초기 기본값 설정 (API 실패 시 이 값이 화면에 나옵니다)
        String currentTemp = "--";
        String weatherIcon = "❓";

        try {
            // 1. 기준 시간 설정
            LocalDateTime now = LocalDateTime.now();
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = now.minusMinutes(45).format(DateTimeFormatter.ofPattern("HH00"));

            // 2. URL 조립
            String url = weatherUrl
                    + "?serviceKey=" + weatherKey
                    + "&numOfRows=10&pageNo=1&dataType=JSON"
                    + "&base_date=" + baseDate
                    + "&base_time=" + baseTime
                    + "&nx=98&ny=76";

            // 3. API 통신
            RestTemplate restTemplate = new RestTemplate();
            String jsonString = restTemplate.getForObject(url, String.class);

            // 4. JSON 파싱 및 안전장치
            if (jsonString != null && !jsonString.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonString);

                // 각 단계마다 path를 확인하여 존재하지 않을 경우를 대비함
                JsonNode items = root.path("response").path("body").path("items").path("item");

                // items가 배열 형태인지 확인 후 순회
                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String category = item.path("category").asText();
                        String value = item.path("obsrValue").asText();

                        if ("T1H".equals(category)) {
                            currentTemp = value;
                            weatherIcon = "☀️"; // 기온 데이터가 있으면 기본 아이콘 설정
                        }
                        if ("PTY".equals(category) && !"0".equals(value)) {
                            weatherIcon = "🌧️"; // 강수 데이터가 있으면 아이콘 변경
                        }
                    }
                }
            }

        } catch (Exception e) {
            // 로그만 남기고 멈추지 않음
            System.err.println("날씨 API 연동 중 오류 발생: " + e.getMessage());
        }

        // 5. 최종 데이터 바인딩 (성공하면 데이터가, 실패하면 초기값이 전달됨)
        model.addAttribute("temp", currentTemp);
        model.addAttribute("weatherIcon", weatherIcon);

        return "index";
    }
}

/*
 * ========== MainController 설명 ==========
 * - 역할: 메인 페이지(index.html) 요청 처리 + 실시간 날씨 데이터 연동
 * - URL 패턴: /
 *
 * [index() 동작 흐름]
 * 1. 기상청 초단기실황 API 호출 (부산 좌표 nx=98, ny=76)
 * 2. JSON 응답에서 T1H(기온), PTY(강수형태) 추출
 * 3. 기온과 날씨 아이콘을 뷰에 전달 (temp, weatherIcon)
 * 4. API 실패 시 기본값("--", "❓") 전달
 *
 * [외부 설정]
 * - weather.api.key: 기상청 API 서비스 키 (application.properties)
 * - weather.api.url: 기상청 API URL (application.properties)
 */
