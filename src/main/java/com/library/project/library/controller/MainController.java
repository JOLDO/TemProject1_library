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
 * 추가 유지보수 사항 발생 시 담당 팀원[이진주]에게 문의주세요🐾
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    // application.properties에서 관리하는 외부 설정값
    @Value("${weather.api.key}")
    private String weatherKey;

    @Value("${weather.api.url}")
    private String weatherUrl;

    // 메인 페이지 호출 및 실시간 날씨 데이터 연동
    @GetMapping("/")
    public String index(Model model) {

        // 1. 기상청 API 호출을 위한 기준 시간(Base_Time) 설정
        // 초단기실황은 매시간 45분마다 생성되므로, 안전하게 현재 시간에서 45분을 차감합니다.
        LocalDateTime now = LocalDateTime.now();
        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = now.minusMinutes(45).format(DateTimeFormatter.ofPattern("HH00"));

        // 2. API 요청 URL 조립 (부산 좌표: nx=98, ny=76)
        String url = weatherUrl
                + "?serviceKey=" + weatherKey
                + "&numOfRows=10&pageNo=1&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=98&ny=76";

        try {
            // 3. 외부 API 통신 (RestTemplate 활용)
            RestTemplate restTemplate = new RestTemplate();
            String jsonString = restTemplate.getForObject(url, String.class);

            // 4. JSON 응답 데이터 파싱 (Jackson 라이브러리)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonString);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            // 초기값 설정
            String currentTemp = "0";
            String weatherIcon = "☀️";

            // 기상 데이터 항목 리스트를 순회하며 필요한 정보 추출
            for (JsonNode item : items) {
                String category = item.path("category").asText();
                String value = item.path("obsrValue").asText();

                // T1H: 기온 데이터
                if ("T1H".equals(category)) {
                    currentTemp = value;
                }
                // PTY: 강수 형태 (비/눈 여부에 따라 아이콘 변경 가능)
                if ("PTY".equals(category) && !"0".equals(value)) {
                    weatherIcon = "🌧️";
                }
            }

            // 5. 뷰(View)로 전달할 데이터 바인딩
            model.addAttribute("temp", currentTemp);
            model.addAttribute("weatherIcon", weatherIcon);

        } catch (Exception e) {
            // 통신 장애 또는 파싱 실패 시 기본값 반환
            model.addAttribute("temp", "--");
            model.addAttribute("weatherIcon", "❓");
            System.err.println("날씨 API 연동 실패: " + e.getMessage());
        }

        return "index";
    }
}
