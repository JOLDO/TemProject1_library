package com.library.project.library.controller;

import com.library.project.library.dto.ApplyDTO;
import com.library.project.library.dto.MemberDTO;
import com.library.project.library.service.ApplyService;
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
@RequestMapping("/apply")
@RequiredArgsConstructor
@Log4j2
public class ApplyController {

    private final ApplyService applyService;

    // 공간 예약 신청 페이지
    @GetMapping("/spaceReservation")
    public String getInfoPage(Model model) {
        model.addAttribute("applyDTO", new ApplyDTO());
        return "apply/spaceReservation";
    }

    // 신청 등록 처리
    @PostMapping("/register")
    public String registerPost(ApplyDTO applyDTO, RedirectAttributes redirectAttributes) {
        log.info("신청서 데이터 전송 시도: " + applyDTO);

        Long ano = applyService.register(applyDTO);

        redirectAttributes.addFlashAttribute("message",
                "[신청번호 : " + ano + "] 신청서 접수가 완료되었습니다!\n신청 내역은 마이페이지에서 확인 가능합니다.");

        return "redirect:/apply/spaceReservation";
    }

//    // 내 시설 예약 신청 내역 목록
//    @GetMapping("/myFacilityList")
//    public String getMyFacilityList(Model model, HttpSession session) {
//
//        Object loginInfo = session.getAttribute("loginInfo");
//
//        if (loginInfo == null) {
//            return "redirect:/member/login";
//        }
//
//        // loginInfo에서 ID 추출 (MemberDTO라고 가정)
//        String mid = ((MemberDTO)loginInfo).getMid();
//        log.info("내 신청 내역 조회 요청 - 회원 ID: " + mid);
//
//        // 3. 해당 사용자의 신청 리스트 조회 (서비스 호출)
//        List<ApplyDTO> applyList = applyService.getApplyListByMid(mid);
//
//        // 4. 뷰로 데이터 전달
//        model.addAttribute("applyList", applyList); // 리스트 전달
//        model.addAttribute("totalCount", applyList.size()); // 총 개수 전달
//        model.addAttribute("mid", mid); // 화면에 누구의 내역인지 표시할 경우 대비
//
//        return "member/myFacilityList";
//    }

    @GetMapping("/myFacilityList")
    public String getMyFacilityList(Model model, HttpSession session) {

        // [임시 코드] 로그인이 자꾸 풀린다면, 테스트를 위해 강제로 세션을 넣어줍니다.
        if (session.getAttribute("loginInfo") == null) {
            log.info("테스트를 위해 임시 세션을 생성합니다.");
            MemberDTO testMember = MemberDTO.builder()
                    .mid("test_user") // 실제 DB에 있는 아이디
                    .mname("테스트유저")
                    .build();
            session.setAttribute("loginInfo", testMember);
        }

        // 이제 인터셉터가 와도 "loginInfo가 있네?" 하고 통과시켜줍니다.
        MemberDTO loginInfo = (MemberDTO) session.getAttribute("loginInfo");
        String mid = loginInfo.getMid();

        List<ApplyDTO> applyList = applyService.getApplyListByMid(mid);
        model.addAttribute("applyList", applyList);
        model.addAttribute("totalCount", applyList.size());

        return "member/myFacilityList";
    }

    // JSON 데이터를 반환하는 상세 조회 API (Ajax용)
    @GetMapping("/readApi")
    @ResponseBody // 💡 페이지가 아닌 데이터를 리턴하게 합니다.
    public ApplyDTO readApi(Long ano) {
        log.info("Ajax 상세 조회 번호: " + ano);

        // 서비스 호출 (기존에 만들어둔 getApply 메서드 활용)
        return applyService.getApply(ano);
    }

}