package com.library.project.library.controller;

import com.library.project.library.config.SessionHelper;
import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.MemberDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.service.BookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Log4j2
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final SessionHelper sessionHelper;

    @GetMapping("/book/booklist")
    public void list(PageRequestDTO pageRequestDTO, Model model, HttpSession session) {
        MemberDTO memberInfo = sessionHelper.getMemberInfo(session);
        PageResponseDTO<BookDTO> responseDTO = bookService.list(
                pageRequestDTO
                , memberInfo == null ? null : memberInfo.getId());
        model.addAttribute("responseDTO", responseDTO);
    }
}

/*
 * ========== BookController 설명 ==========
 * - 역할: 도서 관련 화면(View) 요청을 처리하는 컨트롤러
 * - URL 패턴: /book/booklist
 *
 * [메서드]
 * - list(): GET /book/booklist → 도서 목록 페이지 (booklist.html)
 *   PageRequestDTO로 페이징/검색/정렬 정보를 받아 BookService.list() 호출
 *   결과를 responseDTO로 뷰에 전달
 */