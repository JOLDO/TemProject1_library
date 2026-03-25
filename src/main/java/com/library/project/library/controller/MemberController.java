package com.library.project.library.controller;

import com.library.project.library.dto.InquiryListReplyCountDTO;
import com.library.project.library.dto.MemberDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.service.InquiryService;
import com.library.project.library.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/member")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Member Controller", description = "회원 관련 화면 및 로그인/로그아웃 처리")
public class MemberController {

    private final MemberService memberService;
    private final InquiryService inquiryService;

    @GetMapping("/searchByMid")
    @ResponseBody
// MemberDTO -> List<MemberDTO> 로 변경
    public ResponseEntity<List<MemberDTO>> searchByMid(@RequestParam String mid) {
        try {
            // 서비스에서 검색 결과 리스트를 가져옵니다.
            List<MemberDTO> list = memberService.searchMembers(mid);

            // 결과가 없어도 빈 리스트 [] 를 보냅니다.
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace(); // 서버 콘솔에서 진짜 에러 확인용
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================================
    // 1. 회원가입 화면 (GET) - join.html 연결
    // =====================================================================
    @GetMapping("/join")
    public void joinGet() {
        log.info("MemberController - joinGet() 진입 (join.html 호출)");
    }

    // =====================================================================
    // 2. 회원가입 처리 (POST)
    // =====================================================================
    @Operation(summary = "회원가입 처리 (POST)", description = "회원가입 처리 (POST)")
    @PostMapping("/join")
    public String joinPost(@Valid MemberDTO memberDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        log.info("MemberController - joinPost() 처리 중: " + memberDTO);

        if (bindingResult.hasErrors()) {
            log.info("유효성 검사 에러 발생");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/member/join";
        }

        try {
            memberService.register(memberDTO);
        } catch (Exception e) {
            log.error("중복 아이디 에러: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "mid");
            return "redirect:/member/join";
        }

        redirectAttributes.addFlashAttribute("result", "success");
        return "redirect:/member/login";
    }

    // =====================================================================
    // 3. 로그인 화면 (GET)
    // =====================================================================
    @GetMapping("/login")
    /*public void loginGet() {
        log.info("MemberController - loginGet() 진입");
    }*/
    public String loginGet(
            @CookieValue(value = "savedMid", defaultValue = "") String savedMid,

            //추가1_ljj
            @RequestParam(value = "dest", required = false) String dest, // [1] 주소록 받기
            HttpSession session,

            Model model) {

        //추가2_ljj
        // URL 파라미터로 dest 넘어올 때
        // 예) /member/login?dest=/book/list → 파라미터로 dest 저장
        if (dest != null && !dest.isEmpty()) {
            session.setAttribute("dest", dest);
        }

        log.info("MemberController - loginGet() 진입");
        model.addAttribute("savedMid", savedMid);
        return "member/login";
    }

    // =====================================================================
    // 4. 로그인 처리 (POST) - 세션 방식
    // =====================================================================
    @Operation(summary = "로그인 처리 (POST)", description = "로그인 처리 (POST)")
    /*@PostMapping("/login")
    public String loginPost(String mid, String mpw, HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("로그인 시도 아이디: " + mid);

        try {
            MemberDTO memberDTO = memberService.readOne(mid);

            // 1. 비밀번호 일치 → 로그인 성공
            if (memberDTO.getMpw().equals(mpw)) {
                session.setAttribute("loginInfo", memberDTO); // 세션에 저장
                log.info("로그인 성공! 세션 저장 완료.");

                // 이전에 가려던 페이지(dest)가 있으면 그쪽으로, 없으면 마이페이지로
                String dest = (String) session.getAttribute("dest");
                session.removeAttribute("dest"); // 사용 후 삭제

                if (dest != null) {
                    log.info("이전 목적지로 이동: " + dest);
                    return "redirect:" + dest;
                }
                // mid를 URL에 붙여서 보냄
//                return "redirect:/member/mypage?mid=" + mid;
                // mid 제거
                return "redirect:/member/mypage";
            }
            // 2. 비밀번호 불일치 → 실패
            else {
                redirectAttributes.addFlashAttribute("error", "password");
                return "redirect:/member/login";
            }
        }
        // 3. 아이디 없음 → 실패
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "id");
            return "redirect:/member/login";
        }
    }*/
    @PostMapping("/login")
    public String loginPost(String mid, String mpw,
                            @RequestParam(defaultValue = "false") boolean rememberMe,
                            HttpSession session,
                            HttpServletResponse response,
                            RedirectAttributes redirectAttributes) {
        try {
            MemberDTO memberDTO = memberService.readOne(mid);

            if (memberDTO.getMpw().equals(mpw)) {
                session.setAttribute("loginInfo", memberDTO);
                log.info("로그인 성공! 세션 저장 완료.");

                // 아이디 저장 처리
                if (rememberMe) {
                    Cookie cookie = new Cookie("savedMid", mid);
                    cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                } else {
                    Cookie cookie = new Cookie("savedMid", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }

                String dest = (String) session.getAttribute("dest");
                session.removeAttribute("dest");
                if (dest != null) {
                    log.info("이전 목적지로 이동: " + dest);
                    return "redirect:" + dest;
                }
                return "redirect:/member/mypage";
            } else {
                redirectAttributes.addFlashAttribute("error", "password");
                return "redirect:/member/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "id");
            return "redirect:/member/login";
        }
    }

    // =====================================================================
    // 5. 로그아웃 (GET)
    // - 서버 세션 무효화 + 브라우저 JSESSIONID 쿠키 삭제
    // =====================================================================
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        log.info("MemberController - logout() 실행. 로그아웃 되었습니다.");

        // 1. 서버 세션 무효화
        if (session != null) {
            session.invalidate();
        }

        // 2. 브라우저 JSESSIONID 쿠키 직접 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);       // 즉시 만료
        cookie.setPath("/");       // 세션 생성 시 path와 동일하게
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        redirectAttributes.addFlashAttribute("logout", "success");
        return "redirect:/member/login";
    }

    // =====================================================================
    // 6. 마이페이지 (GET)
    // - 세션에서 loginInfo 검증 후 진입
    // =====================================================================
    @Operation(summary = "마이페이지 (GET)", description = "마이페이지 (GET)")
    @GetMapping("/mypage")
//    public String myPage(String mid, HttpSession session, Model model) {
    public String myPage(HttpSession session, Model model) {
        log.info("MemberController - 마이페이지 진입 되었습니다.");
        // 세션 검증 - loginInfo 없으면 로그인 페이지로
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        String mid = loginInfo.getMid();
        // 인터셉터 에 기능이 있지만 혹시나
//        사용자 접근
//              ↓
//        인터셉터가 먼저 loginInfo 체크
//              ↓
//        loginInfo 없으면 → 인터셉터가 /member/login으로 보냄 (컨트롤러 진입 못함)
//              ↓
//        loginInfo 있으면 → 컨트롤러 진입
//              ↓
//        myPage() 실행 (여기까지 왔으면 loginInfo는 무조건 존재)
        if (loginInfo == null) {
            return "redirect:/member/login";
        }

        MemberDTO memberDTO = memberService.readOne(mid);
        model.addAttribute("dto", memberDTO);
        return "member/mypage";
    }

    // =====================================================================
    // 7. 정보 수정 화면 (GET)
    // =====================================================================
    /*@GetMapping("/modify")
    public void modifyGet(String mid, Model model) {
        log.info("MemberController - modifyGet() 호출: " + mid);
        MemberDTO memberDTO = memberService.readOne(mid);
        model.addAttribute("dto", memberDTO);
    }*/
    @GetMapping("/modify")
    public String modifyGet(HttpSession session, Model model) {
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        String mid = loginInfo.getMid();
        log.info("MemberController - modifyGet() 호출: " + mid);
        MemberDTO memberDTO = memberService.readOne(mid);
        model.addAttribute("dto", memberDTO);
        return "member/modify";
    }

    // =====================================================================
    // 8. 정보 수정 처리 (POST)
    // - 수정 완료 후 세션 loginInfo도 최신 정보로 갱신
    // =====================================================================
    @Operation(summary = "회원 정보 수정 처리 (POST)", description = "회원 정보 수정 처리 (POST)")
    @PostMapping("/modify")
    public String modifyPost(@Valid MemberDTO memberDTO,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        log.info("MemberController - modifyPost() 진행: " + memberDTO);

        if (bindingResult.hasErrors()) {
            log.info("수정 유효성 에러 상세내용: " + bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("mid", memberDTO.getMid());
            return "redirect:/member/modify";
        }

        try {
            memberService.modify(memberDTO);

            // 세션 loginInfo 갱신 (수정된 정보로 업데이트)
            MemberDTO updatedMember = memberService.readOne(memberDTO.getMid());
            session.setAttribute("loginInfo", updatedMember);
            log.info("세션 loginInfo 갱신 완료: " + updatedMember.getMid());

        } catch (Exception e) {
            log.error("수정 실패: " + e.getMessage());
//            return "redirect:/member/modify?mid=" + memberDTO.getMid();
            return "redirect:/member/modify";
        }

        redirectAttributes.addFlashAttribute("result", "modified");
//        return "redirect:/member/mypage?mid=" + memberDTO.getMid();
        return "redirect:/member/mypage";
    }

    // =====================================================================
    // 9. 아이디 중복 체크 (AJAX)
    // =====================================================================
    @Operation(summary = "아이디 중복 체크 (GET)", description = "아이디 중복 체크 (GET)")
    @GetMapping("/checkId")
    @ResponseBody
    public String checkId(String mid) {
        boolean exists = memberService.checkId(mid);
        return exists ? "exist" : "ok";
    }

    // =====================================================================
    // 10. 이메일 중복 체크 (AJAX)
    // =====================================================================
    @GetMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(String email) {
        return memberService.checkEmail(email) ? "exist" : "ok";
    }

    /*// =====================================================================
    // 11. 아이디/비밀번호 찾기 페이지 (GET)
    // =====================================================================
    @GetMapping("/find")
    public String findGet() {
        return "member/find";
    }

    // =====================================================================
    // 12. 아이디 찾기 처리 (POST)
    // =====================================================================
    @PostMapping("/find-id")
    public String findIdPost(String mname, String email, RedirectAttributes redirectAttributes) {
        String mid = memberService.findId(mname, email);
        if (mid != null) {
            redirectAttributes.addFlashAttribute("foundMid", mid);
        } else {
            redirectAttributes.addFlashAttribute("errorId", "fail");
        }
        return "redirect:/member/find";
    }

    // =====================================================================
    // 13. 비밀번호 찾기 - 본인 확인 후 변경 페이지 이동 (POST)
    // =====================================================================
    @PostMapping("/find-pw")
    *//*public String findPwPost(String mid, String email, Model model, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 찾기 시도: mid=" + mid + ", email=" + email);

        if (memberService.checkMemberForPw(mid, email)) {
            model.addAttribute("mid", mid);
            return "member/change-pw";
        }

        log.warn("비밀번호 찾기 실패: 정보 불일치");
        redirectAttributes.addFlashAttribute("errorPw", "fail");
        return "redirect:/member/find";
    }*//*
    *//*public String findPwPost(String mid, String email, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 찾기 시도: mid=" + mid + ", email=" + email);

        if (memberService.checkMemberForPw(mid, email)) {
            redirectAttributes.addFlashAttribute("verifiedMid", mid);
            return "redirect:/member/modify";  // modify 페이지로 이동 + 모달 자동 오픈
        }

        log.warn("비밀번호 찾기 실패: 정보 불일치");
        redirectAttributes.addFlashAttribute("errorPw", "fail");
        return "redirect:/member/find";
    }*//*
    public String findPwPost(String mid, String email, RedirectAttributes redirectAttributes) {
        if (memberService.checkMemberForPw(mid, email)) {
            redirectAttributes.addFlashAttribute("verifiedMid", mid);
            return "redirect:/member/reset-pw";  // 전용 페이지로!
        }
        redirectAttributes.addFlashAttribute("errorPw", "fail");
        return "redirect:/member/find";
    }

    // 비밀번호 재설정 화면 GET
    @GetMapping("/reset-pw")
    public String resetPwGet(Model model) {
        return "member/reset-pw";
    }

    // 비밀번호 재설정 처리 POST
    @PostMapping("/reset-pw")
    public String resetPwPost(String mid, String newPw, RedirectAttributes redirectAttributes) {
        memberService.updatePassword(mid, newPw);
        redirectAttributes.addFlashAttribute("result", "pwChanged");
        return "redirect:/member/login";
    }

    // =====================================================================
    // 14. 비밀번호 실제 변경 처리 (POST)
    // =====================================================================
    @PostMapping("/change-pw")
    public String changePwPost(String mid, String newPw, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 변경 처리 시작: mid=" + mid);
        memberService.updatePassword(mid, newPw);
        redirectAttributes.addFlashAttribute("result", "pwChanged");
        return "redirect:/member/login";
    }*/

    // =====================================================================
    // 11. 아이디 찾기 페이지 (GET)
    // =====================================================================
    @GetMapping("/find")
    public String findGet(Model model) {
        log.info("아이디 찾기 페이지 (GET)");
        return "member/find";
    }

    // =====================================================================
    // 12. 아이디 찾기 처리 (POST)
    // =====================================================================
    @PostMapping("/find-id")
    public String findIdPost(String mname, String email, RedirectAttributes redirectAttributes) {
        log.info("아이디 찾기 처리 (POST)" + mname);
        String mid = memberService.findId(mname, email);
        if (mid != null) {
            redirectAttributes.addFlashAttribute("foundMid", mid);
        } else {
            redirectAttributes.addFlashAttribute("errorId", "fail");
        }
        return "redirect:/member/find";
    }

    // =====================================================================
    // 13. 비밀번호 찾기 페이지 (GET)
    // =====================================================================
    @GetMapping("/find-pw-page")
    public String findPwPageGet(Model model) {
        log.info("비밀번호 찾기 페이지 (GET)");
        return "member/find-pw";
    }

    // =====================================================================
    // 14. 비밀번호 찾기 본인확인 처리 (POST)
    // =====================================================================
    @PostMapping("/find-pw")
    public String findPwPost(String mid, String email, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 찾기 시도: mid=" + mid + ", email=" + email);

        if (memberService.checkMemberForPw(mid, email)) {
            redirectAttributes.addFlashAttribute("verifiedMid", mid);
            return "redirect:/member/reset-pw";
        }

        log.warn("비밀번호 찾기 실패: 정보 불일치");
        redirectAttributes.addFlashAttribute("errorPw", "fail");
        return "redirect:/member/find-pw-page";
    }

    // =====================================================================
    // 15. 비밀번호 재설정 화면 (GET)
    // =====================================================================
    @GetMapping("/reset-pw")
    public String resetPwGet(Model model) {
        log.info("비밀번호 재설정 화면 (GET)");
        return "member/reset-pw";
    }

    // =====================================================================
    // 16. 비밀번호 재설정 처리 (POST)
    // =====================================================================
    @PostMapping("/reset-pw")
    public String resetPwPost(String mid, String newPw, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 재설정 처리 시작: mid=" + mid);
        memberService.updatePassword(mid, newPw);
        redirectAttributes.addFlashAttribute("result", "pwChanged");
        return "redirect:/member/login";
    }

    // =====================================================================
    // 17. 비밀번호 실제 변경 처리 (POST) - 로그인 후 정보수정에서 사용
    // =====================================================================
    @PostMapping("/change-pw")
    public String changePwPost(String mid, String newPw, RedirectAttributes redirectAttributes) {
        log.info("비밀번호 변경 처리 시작: mid=" + mid);
        memberService.updatePassword(mid, newPw);
        redirectAttributes.addFlashAttribute("result", "pwChanged");
        return "redirect:/member/login";
    }



    // =====================================================================
    // 18. 나의 문의 내역 (GET) - 세션 방식 적용
    // =====================================================================
    @GetMapping("/inquiryList")
    public String myList(PageRequestDTO pageRequestDTO, Model model, HttpSession session) {
        log.info(">>>> [MemberController] 내 문의 내역 페이지 접속 중...");

        // 1. 세션에서 로그인 정보 꺼내기
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");

        // 2. 비로그인 체크 (인터셉터가 있지만 안전을 위해 추가)
        if (loginInfo == null) {
            log.info(">>>> 로그인 정보 없음 -> 로그인 페이지로 이동");
            return "redirect:/member/login?dest=/member/inquiryList";
        }

        String mid = loginInfo.getMid();
        String mname = loginInfo.getMname();

        log.info(">>>> 조회 아이디(mid): " + mid);

        // 3. 서비스 호출 (Querydsl의 searchMyList가 이 mid를 사용해 내 글만 가져옵니다)
        PageResponseDTO<InquiryListReplyCountDTO> responseDTO =
                inquiryService.getMyInquiryList(mid, pageRequestDTO);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("writer", mname); // 화면에 표시될 이름

        // 4. HTML 경로: src/main/resources/templates/inquiry/myList.html
        return "inquiry/myList";
    }
    /*public String myList(HttpSession session, PageRequestDTO pageRequestDTO, Model model) {
        log.info(">>>> 내 문의 내역 페이지 접속 중...");

        // 1. 로그인 체크 (테스트용 user1)
//        String writer = "user1";
//        if (principal != null) {
//            writer = principal.getName();
//        }
        // 황혜은 수정 20260324
        // 1. 세션에서 로그인 정보 꺼내기 (인터셉터가 체크해주므로 바로 꺼내면 됨)
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        String mid = loginInfo.getMid();
        String mname = loginInfo.getMname();
        log.info("문의내역 mid" + mid);
        // 2. 서비스 호출
        PageResponseDTO<InquiryListReplyCountDTO> responseDTO =
                inquiryService.getMyInquiryList(mid, pageRequestDTO);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("writer", mname);

        // 3. 리턴 경로 (HTML 파일 위치)
        // src/main/resources/templates/inquiry/myList.html 가 있다면 아래가 맞음
        return "inquiry/myList";
    }*/
}

/*
 * ========== MemberController 변경 이력 ==========
 *
 * [20260323 수정 내용]
 * 1. logout()
 *    - session.invalidate() 로 서버 세션 무효화
 *    - Cookie maxAge=0 으로 브라우저 JSESSIONID 쿠키 직접 삭제
 *
 * 2. myPage()
 *    - void → String 반환으로 변경
 *    - 세션 loginInfo 검증 추가 (없으면 로그인 페이지로 redirect)
 *
 * 3. modifyPost()
 *    - HttpSession 파라미터 추가
 *    - DB 수정 완료 후 세션 loginInfo 최신 정보로 갱신
 *    - (갱신 안 하면 수정 후 마이페이지 redirect 시 loginInfo null로 튕김)
 *
 * 4. loginPost()
 *    - dest 기능 유지 (인터셉터에서 저장한 이전 목적지로 로그인 후 이동)
 *
 * ========== MemberController 메서드 목록 ==========
 * - joinGet()       : GET  /member/join       → 회원가입 화면
 * - joinPost()      : POST /member/join       → 회원가입 처리
 * - loginGet()      : GET  /member/login      → 로그인 화면
 * - loginPost()     : POST /member/login      → 로그인 처리 (세션 저장 + dest 처리)
 * - logout()        : GET  /member/logout     → 로그아웃 (세션 무효화 + 쿠키 삭제)
 * - myPage()        : GET  /member/mypage     → 마이페이지 (세션 검증)
 * - modifyGet()     : GET  /member/modify     → 정보 수정 화면
 * - modifyPost()    : POST /member/modify     → 정보 수정 처리 (세션 갱신)
 * - checkId()       : GET  /member/checkId    → 아이디 중복 체크 (AJAX)
 * - checkEmail()    : GET  /member/checkEmail → 이메일 중복 체크 (AJAX)
 * - findGet()       : GET  /member/find       → 아이디/비밀번호 찾기 페이지
 * - findIdPost()    : POST /member/find-id    → 아이디 찾기 처리
 * - findPwPost()    : POST /member/find-pw    → 비밀번호 찾기 본인확인
 * - changePwPost()  : POST /member/change-pw  → 비밀번호 변경 처리
 */