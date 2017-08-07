package com.github.tangr1.lego.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @GetMapping
    public String home(final Principal principal) {
        if (principal == null) {
            return "你好，访客";
        } else {
            return "你好，" + principal.getName();
        }
    }
}
