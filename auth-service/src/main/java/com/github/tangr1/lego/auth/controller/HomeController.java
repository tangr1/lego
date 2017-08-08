package com.github.tangr1.lego.auth.controller;

import com.github.tangr1.lego.auth.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping
    public String home(final Principal principal) {
        if (principal == null) {
            return "Hi, visitor";
        } else {
            return "Hi, " + principal.getName();
        }
    }


    @GetMapping(path = "/user")
    public UserDetails getCurrentAccount(Principal principal) {
        return userDetailsService.loadUserByUsername(principal.getName());
    }
}