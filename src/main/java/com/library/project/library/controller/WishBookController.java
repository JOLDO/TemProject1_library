package com.library.project.library.controller;


import com.library.project.library.dto.WishBookDTO;
import com.library.project.library.service.WishBookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/apply/wishBook")
@Log4j2
@RequiredArgsConstructor
public class WishBookController {

    //[GET] 비치희망도서 신청 페이지 이동
    @GetMapping("")
    public String wishBookHome() {
        log.info("비치희망도서 신청 페이지(wishBook.html) 접속");
        return "apply/wishBook";
    }

    private final WishBookService wishBookService;

    //[POST] 비치희망도서 신청서 등록 처리
    @PostMapping("/register")
    public String registerWishBook(WishBookDTO wishBookDTO, RedirectAttributes redirectAttributes) {

        //로그인 기능 구현 시 주석 해제하기
//         public String registerWishBook(WishBookDTO wishBookDTO, HttpSession session, RedirectAttributes redirectAttributes) {
//         String loginId = (String) session.getAttribute("mid");
//         wishBookDTO.setMid(loginId);

        log.info("==========================================");
        log.info("   [희망도서 신청 데이터 수신 확인]   ");
        log.info("==========================================");
        log.info("1. 신청자명: " + wishBookDTO.getWishApplicantName());
        log.info("2. 연락처: " + wishBookDTO.getWishPhone());
        log.info("3. 도서명: " + wishBookDTO.getWishBookTitle());
        log.info("4. 저자명: " + wishBookDTO.getWishAuthor());
        log.info("5. 출판사: " + wishBookDTO.getWishPublisher());

        // 이미지 파일 처리 확인
        if (wishBookDTO.getWishBookImage() != null && !wishBookDTO.getWishBookImage().isEmpty()) {
            log.info("6. 첨부파일 있음: " + wishBookDTO.getWishBookImage().getOriginalFilename());
            log.info("7. 파일 크기: " + wishBookDTO.getWishBookImage().getSize() + " bytes");
        } else {
            log.info("6. 첨부파일 없음 (선택사항)");
        }
        log.info("==========================================");

        wishBookService.register(wishBookDTO);

        // 화면에 띄울 성공 메시지 전달
        redirectAttributes.addFlashAttribute("message",
                "[" + wishBookDTO.getWishBookTitle() + "] 도서 신청이 완료되었습니다.\n신청 현황은 내 서재에서 확인 가능합니다.");

        // 신청 완료 후 다시 신청 페이지로 리다이렉트
        return "redirect:/apply/wishBook";
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