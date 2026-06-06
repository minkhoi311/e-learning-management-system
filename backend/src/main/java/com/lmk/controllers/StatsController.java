package com.lmk.controllers;

import com.lmk.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/stats")
    public String adminStats(Model model) {
        model.addAttribute("stats", statsService.getAdminStats());
        return "stats-admin";
    }
}