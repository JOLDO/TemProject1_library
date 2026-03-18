package com.library.project.library.controller;


import com.library.project.library.dto.ApplyDTO;
import com.library.project.library.service.ApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/apply")
@RequiredArgsConstructor // 서비스 주입을 위해 추가
@Log4j2
public class ApplyController {

    private final ApplyService applyService; // 서비스 주입

    @GetMapping("/spaceReservation")
    public String getInfoPage(Model model) {
        model.addAttribute("applyDTO", new ApplyDTO());
        return "apply/spaceReservation";
    }

    @PostMapping("/register")
    public String registerPost(ApplyDTO applyDTO, RedirectAttributes redirectAttributes) {
        log.info("신청서 데이터 전송 시도: " + applyDTO);

        // 실제 DB 저장 서비스 호출
        Long ano = applyService.register(applyDTO);

        // 성공 메시지에 신청 번호나 줄바꿈을 섞어서 전달
        redirectAttributes.addFlashAttribute("message",
                "[신청번호 : " + ano + "] 신청서 접수가 완료되었습니다!\n담당자 확인 후 연락드리겠습니다.");

        return "redirect:/apply/spaceReservation";
    }
}