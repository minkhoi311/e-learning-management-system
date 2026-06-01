/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;
import com.lmk.services.StatsService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiStatsController {

    @Autowired
    private StatsService statsService;

    // GET /api/stats/overview — Instructor xem thống kê của mình
    // @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/secure/stats/overview")
    public ResponseEntity<Object> instructorStats(Principal principal) {

        if (principal == null)
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"),HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(this.statsService.getInstructorStats(principal.getName()),HttpStatus.OK);
    }

    // GET /api/stats/admin/overview — Admin xem báo cáo tổng thể
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/secure/stats/admin/overview")
    public ResponseEntity<Map<String, Object>> adminStats() {
        return new ResponseEntity<>(this.statsService.getAdminStats(),HttpStatus.OK);
    }
}
