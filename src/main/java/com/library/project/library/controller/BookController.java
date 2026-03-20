package com.library.project.library.controller;

import com.library.project.library.dto.BookDTO;
import com.library.project.library.dto.PageRequestDTO;
import com.library.project.library.dto.PageResponseDTO;
import com.library.project.library.service.BookService;
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

    @GetMapping("/book/booklist")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<BookDTO> responseDTO = bookService.list(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
    }
}