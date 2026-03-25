package com.library.project.library.controller;

import com.library.project.library.dto.ApplyDTO;
import com.library.project.library.dto.MemberDTO;
import com.library.project.library.service.ApplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
//@RequestMapping("/apply")
@RequiredArgsConstructor
@Log4j2
public class ApplyController {

    private final ApplyService applyService;

    // 공간 예약 신청 페이지
    @GetMapping("/apply/spaceReservation")
    public String getInfoPage(Model model) {
        // 폼 바인딩을 위한 빈 객체 전달
        model.addAttribute("applyDTO", new ApplyDTO());
        return "apply/spaceReservation";
    }

    // [수정됨] 신청 등록 처리
    @PostMapping("/apply/register")
    public String registerPost(ApplyDTO applyDTO, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1. 로그인 여부 재확인 (서버 측 안전장치)
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        if (loginInfo == null) {
            log.warn("비로그인 사용자의 접근 차단");
            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            return "redirect:/member/login";
        }

        // 2. 현재 로그인한 사용자의 ID를 DTO에 세팅 (누가 신청했는지 식별)
        applyDTO.setMid(loginInfo.getMid());

        log.info("신청서 데이터 전송 시도 - 작성자: " + loginInfo.getMid());
        log.info("상세 데이터: " + applyDTO);

        // 3. 서비스 호출 및 등록
        Long ano = applyService.register(applyDTO);

        // 4. 완료 메시지 전달
        redirectAttributes.addFlashAttribute("message",
                "[신청번호 : " + ano + "] 신청서 접수가 완료되었습니다!\n신청 내역은 마이페이지에서 확인 가능합니다.");

        return "redirect:/apply/spaceReservation";
    }


    // [추가] 상세 내역 조회를 위한 Ajax 전용 API
    @ResponseBody // 페이지 이동이 아니라 '데이터(JSON)'만 보내겠다는 선언입니다.
    @GetMapping("/member/readApi") // HTML의 fetch('/apply/readApi?ano=' + ano) 와 주소를 맞춰줍니다.
    public ApplyDTO readApi(Long ano) {
        log.info("시설 대관 상세 조회 요청 - 번호: " + ano);

        // 1. 서비스에서 해당 번호(ano)의 데이터를 가져옵니다.
        ApplyDTO applyDTO = applyService.getApply(ano);

        // 2. 만약 데이터가 없으면 로그를 남깁니다.
        if(applyDTO == null) {
            log.error(ano + "번 신청 내역을 찾을 수 없습니다.");
        }

        // 3. 데이터를 JSON 형태로 브라우저에 던져줍니다.
        return applyDTO;
    }

    // 내 시설 예약 신청 내역 목록 (기존 코드 유지)
    @GetMapping("member/myFacilityList")
    public String getMyFacilityList(HttpSession session, Model model) {

        // 1. 세션에서 로그인 정보 꺼내기 (인터셉터가 체크해주므로 바로 꺼내면 됨)
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        String mid = loginInfo.getMid();

        // 2. 본인 로직 작성 (진주님 작업: 해당 mid로 신청 내역 조회)
        List<ApplyDTO> applyList = applyService.getApplyListByMid(mid);
        model.addAttribute("applyList", applyList);
        model.addAttribute("totalCount", applyList.size());

        // 3. 가이드 필수 항목 (화면 레이아웃용)
        model.addAttribute("mid", mid); // 사이드 메뉴 등에 내 아이디 표시용
        model.addAttribute("pageTitle", "시설 예약 내역"); // 상단 빨간 배너 타이틀용

        return "member/myFacilityList"; // 가이드에서 지정한 경로
    }
}

/*
 * ========== ApplyController 설명 ==========
 * - 역할: 시설 대관 신청 관련 화면 + 등록 처리 컨트롤러
 * - URL 패턴: /apply/**
 *
 * [메서드]
 * - getInfoPage(): GET /apply/spaceReservation → 대관 신청 페이지 (spaceReservation.html)
 * - registerPost(): POST /apply/register → 대관 신청서 등록 처리 (성공 시 신청번호 포함 메시지 전달)
 * - getMyFacilityList(): GET /apply/myFacilityList → 내 시설 예약 신청 내역 목록 (member/myFacilityList.html)
 * - readApi(): GET /apply/readApi → Ajax용 신청 상세 조회 API (JSON 반환)
 */