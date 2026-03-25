package com.library.project.library.controller;

import com.library.project.library.dto.MemberDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RentalViewController {

    // 관리자용 - ADMIN만 접근 가능
    @GetMapping("/rentals")
    public String adminRentals(HttpSession session) {
        Object loginInfo = session.getAttribute("loginInfo");

        // 로그인 안 했으면 로그인 페이지로
        if (loginInfo == null) {
            return "redirect:/member/login";
        }

        // ADMIN이 아니면 회원 페이지로
        MemberDTO memberDTO = (MemberDTO) loginInfo;
        if (!memberDTO.getRole().equals("ADMIN")) {
            return "redirect:/user_rentals";
        }

        return "rental/rentals";
    }

    // 회원용 - 로그인만 하면 접근 가능
    @GetMapping("/user_rentals")
    public String userRentals(HttpSession session) {
        Object loginInfo = session.getAttribute("loginInfo");

        // 로그인 안 했으면 로그인 페이지로
        if (loginInfo == null) {
            return "redirect:/member/login";
        }

        return "rental/user_rentals";
    }
}

/*
 * ========== RentalViewController 설명 ==========
 * - 역할: 대출 관련 화면(View) 이동만 담당하는 컨트롤러 (비즈니스 로직 없음)
 *
 * [메서드]
 * - adminRentals(): GET /rentals → 관리자용 대출 관리 화면 (rental/rentals.html)
 * - userRentals(): GET /user_rentals → 회원용 내 대출 현황 화면 (rental/user_rentals.html)
 */
