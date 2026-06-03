package com.lmk.controllers;

import com.lmk.services.StatsService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiStatsController {

    @Autowired
    private StatsService statsService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/secure/stats/overview")
    public ResponseEntity<Map<String, Object>> instructorStats(Principal principal) {
        if (principal == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(this.statsService.getInstructorStats(principal.getName()),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/secure/stats/admin/overview")
    public ResponseEntity<Map<String, Object>> adminStats() {
        return new ResponseEntity<>(this.statsService.getAdminStats(),HttpStatus.OK);
    }
}