package com.lmk.controllers;

import com.lmk.services.StatsService;
import java.security.Principal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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