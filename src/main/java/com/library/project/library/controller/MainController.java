package com.library.project.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/") // 로컬호스트 접속 시 바로 호출
    public String index() {
        return "index";
    }
}
