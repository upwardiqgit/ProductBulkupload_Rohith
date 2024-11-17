package com.product.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }
}
