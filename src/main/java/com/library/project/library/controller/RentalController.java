package com.library.project.library.controller;

import com.library.project.library.dto.MemberDTO;
import com.library.project.library.dto.rentalDto.RentalRequestDTO;
import com.library.project.library.dto.rentalDto.RentalResponseDTO;
import com.library.project.library.dto.rentalDto.ReturnRequestDTO;
import com.library.project.library.service.RentalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;


     @GetMapping("/rentals")
    public String rentalList(HttpSession session, Model model) {
        // 인터셉터가 이미 체크했지만 이중 안전장치
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        // 관리자 정보 활용 가능
        model.addAttribute("admin", loginInfo.getMname());

        // 본인 로직 작성
        return "/rentals";
    }

    /**
     * 📌 도서 대출
     */
    @PostMapping
    public ResponseEntity<String> rentBook(
            @RequestBody RentalRequestDTO dto) {

        rentalService.rentBook(dto);
        return ResponseEntity.ok("대출 완료");
    }


    /**
     * 📌 도서 반납
     */
    @PostMapping("/return")
    public ResponseEntity<String> returnBook(
            @RequestBody ReturnRequestDTO dto) {

        rentalService.returnBook(dto);
        return ResponseEntity.ok("반납 완료");
    }

    // 재대출
    @PostMapping("/renew/{id}")
    public ResponseEntity<String> renewBook(@PathVariable Long id){
        rentalService.renewBook(id);
        return ResponseEntity.ok("재대출 완료");
    }

    /**
     * 📌 사용자 대출 목록 조회
     */
    @GetMapping("/member/{MemberId}")
    public ResponseEntity<List<RentalResponseDTO>> getUserRentals(
            @PathVariable Long MemberId) {

        List<RentalResponseDTO> rentals =
                rentalService.getUserRentals(MemberId);

        return ResponseEntity.ok(rentals);
    }


    /**
     * 📌 인기 도서 (대출 많은 순)
     */
    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getMostRentedBooks() {

        List<Object[]> result =
                rentalService.getMostRentedBooks();

        return ResponseEntity.ok(result);
    }

}

/*
 * ========== RentalController 설명 ==========
 * - 역할: 도서 대출/반납/재대출 REST API 컨트롤러
 * - URL 패턴: /api/rentals/**
 * - 쓰이는 곳: 프론트엔드 JavaScript에서 AJAX 호출
 *
 * [메서드]
 * - rentalPage(): GET /api/rentals/rental → 대출 페이지 뷰 반환
 * - rentBook(): POST /api/rentals → 도서 대출 처리 (JSON body: memberId, bookId)
 * - returnBook(): POST /api/rentals/return → 도서 반납 처리 (JSON body: rentalId)
 * - renewBook(): POST /api/rentals/renew/{id} → 재대출 처리
 * - getUserRentals(): GET /api/rentals/member/{MemberId} → 특정 회원의 대출 목록 조회
 * - getMostRentedBooks(): GET /api/rentals/stats → 인기 도서 통계 (대출 많은 순)
 */
