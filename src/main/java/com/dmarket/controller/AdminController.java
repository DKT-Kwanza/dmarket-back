package com.dmarket.controller;

import com.dmarket.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    AdminService adminService;

    @GetMapping("/GM")
    public String adminGMP() {
        return "Admin GM Page";
    }
    @GetMapping("/PM")
    public String adminPMP() {
        return "Admin PM Page";
    }
    @GetMapping("/SM")
    public String adminSMP() {
        return "Admin SM Page";
    }
}
