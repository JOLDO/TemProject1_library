package com.library.project.library.controller;


import com.library.project.library.dto.ApplyDTO;
import com.library.project.library.dto.MemberDTO;
import com.library.project.library.dto.WishBookDTO;
import com.library.project.library.service.WishBookService;
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

import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequiredArgsConstructor
public class WishBookController {

    private final WishBookService wishBookService;

    // [GET] 비치희망도서 신청 안내 페이지 이동 (기존 경로 유지)
    @GetMapping("/apply/wishBook")
    public String wishBookHome() {
        log.info("비치희망도서 신청 페이지 접속");
        return "apply/wishBook";
    }

    // [GET] 로그인 체크 전용 경로 (시설 예약과 동일한 로직)
    // WishBookController 내의 checkAuth 메서드
    // WishBookController 내부

    @GetMapping("/apply/wishBook/check-auth")
    public String checkAuth(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loginInfo") == null) {
            // MemberController의 로직에 맞춰 "dest"라는 이름으로 세션에 저장
            // 돌아왔을 때 모달을 자동으로 띄우기 위해 쿼리 스트링 추가
            session.setAttribute("dest", "/apply/wishBook?openModal=true");

            log.info("비로그인 사용자의 목적지(dest) 세션 저장: /apply/wishBook?openModal=true");

            redirectAttributes.addFlashAttribute("message", "로그인이 필요한 서비스입니다.");
            return "redirect:/member/login";
        }
        return "redirect:/apply/wishBook?openModal=true";
    }

    // [POST] 비치희망도서 신청서 등록 처리
    @PostMapping("/apply/wishBook/register") // HTML의 th:action 경로와 일치시킴
    public String registerWishBook(WishBookDTO wishBookDTO, HttpSession session, RedirectAttributes redirectAttributes) {

        // 1. 세션에서 로그인한 사용자 정보 가져오기
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginInfo");

        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("message", "로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
            return "redirect:/member/login";
        }

        // 2. DTO에 로그인 아이디 설정 (DB 저장을 위해)
        wishBookDTO.setMid(loginUser.getMid());

        log.info("==========================================");
        log.info("   [희망도서 신청 데이터 수신 및 저장]   ");
        log.info("신청 아이디: " + wishBookDTO.getMid());
        log.info("도서명: " + wishBookDTO.getWishBookTitle());
        log.info("==========================================");

        // 3. 서비스 호출 (DB 저장)
        wishBookService.register(wishBookDTO);

        // 4. 성공 메시지 전달
        redirectAttributes.addFlashAttribute("message",
                "[" + wishBookDTO.getWishBookTitle() + "] 도서 신청이 완료되었습니다.\n신청 현황은 내 서재에서 확인 가능합니다.");

        return "redirect:/apply/wishBook";
    }

    // [GET] 내 희망도서 신청 내역 조회
    // [GET] 내 희망도서 신청 내역 조회
    @GetMapping("member/applyMyWishBook")
    public String getMyWishBookList(Model model, HttpSession session) {

        // 1. 세션에서 로그인 정보 가져오기
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginInfo");

        // 2. 로그인 안 되어 있을 시 로그인 페이지로 (안전장치)
        if (loginUser == null) {
            session.setAttribute("dest", "/member/applyMyWishBook"); // 마이페이지 내역으로 바로 오게 저장
            return "redirect:/member/login";
        }

        String mid = loginUser.getMid();
        log.info("▶▶▶ 희망도서 신청 내역 조회 요청 - 회원 ID: " + mid);

        // 3. 실제 서비스 연결: 로그인한 사용자의 ID로 신청 리스트 조회
        // wishBookService.getList(mid)가 List<WishBookDTO>를 반환한다고 가정합니다.
        List<WishBookDTO> wishList = wishBookService.getList(mid);

        // 4. 모델에 데이터 담기
        model.addAttribute("wishList", wishList);
        model.addAttribute("totalCount", wishList.size());
        model.addAttribute("mid", mid);

        log.info("조회된 신청 건수: " + wishList.size() + "건");

        return "member/applyMyWishBook";
    }
}

/*
 * ========== WishBookController 설명 ==========
 * - 역할: 비치희망도서 신청 관련 화면 + 등록 처리 컨트롤러
 * - URL 패턴: /apply/wishBook/**
 *
 * [메서드]
 * - wishBookHome(): GET /apply/wishBook → 희망도서 신청 페이지 (wishBook.html)
 * - registerWishBook(): POST /apply/wishBook/register → 희망도서 신청서 등록 처리
 *   → 신청 데이터 로그 출력 + DB 저장 + 성공 메시지 전달 후 신청 페이지로 리다이렉트
 */