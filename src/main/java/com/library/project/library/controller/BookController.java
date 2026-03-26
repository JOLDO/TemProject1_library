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
    public void list(PageRequestDTO pageRequestDTO, Model model, HttpSession session) { //size와, page가 각각 10과 1로 기본 세팅된 dto를 읽어옴(쿼리스트링이 없어도 기본값이 정해져 있음)
        MemberDTO memberInfo = sessionHelper.getMemberInfo(session);    //로그인 정보 조회 (비로그인이면 null), 책 리스트는 회원, 비회원 상관없이 확인 할수 있게 사용
        PageResponseDTO<BookDTO> responseDTO = bookService.list(
                pageRequestDTO  //데이터를 읽어오며 request를 세팅 할거임
                , memberInfo == null ? null : memberInfo.getId());
        model.addAttribute("responseDTO", responseDTO); //html에서 페이지네이션을 적용하여 사용하기 위해
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