package com.spring.ai.firstproject.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String chatPage() {
        return "chat-enhanced";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload-enhanced";
    }
}
