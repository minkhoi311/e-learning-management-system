/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.services.StatsService;
import java.security.Principal;
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

    // Admin xem báo cáo tổng quan
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public String adminStats(Model model) {
        model.addAttribute("stats", statsService.getAdminStats());
        return "stats-admin";
    }

    // Giảng viên xem thống kê của mình
    // @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/stats/my")
    public String instructorStats(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        model.addAttribute("stats", statsService.getInstructorStats(principal.getName()));
        return "stats-instructor";
    }
}
