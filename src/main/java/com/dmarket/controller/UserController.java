package com.dmarket.controller;

import com.dmarket.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    UserService userService;

    @GetMapping("/user")
    public String userP() {
        return "User Page";
    }

    @GetMapping("/admin/GM")
    public String adminGMP() {
        return "Admin GM Page";
    }
    @GetMapping("/admin/PM")
    public String adminPMP() {
        return "Admin PM Page";
    }
    @GetMapping("/admin/SM")
    public String adminSMP() {
        return "Admin SM Page";
    }
}
