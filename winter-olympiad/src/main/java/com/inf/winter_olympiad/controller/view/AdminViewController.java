package com.inf.winter_olympiad.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    @GetMapping("/admin/dashboard")
    public String getDashboard() {
        return "admin/dashboard";
    }
}

