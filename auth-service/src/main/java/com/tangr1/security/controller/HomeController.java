package com.tangr1.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @GetMapping
    public String home(final Principal principal) {
        if (principal == null) {
            return "Hello anonymous";
        } else {
            return "Hello " + principal.getName();
        }
    }
}
