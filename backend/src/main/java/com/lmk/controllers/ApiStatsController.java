package com.lmk.controllers;

import com.lmk.services.StatsService;
import java.security.Principal;
import java.time.Year;
import java.util.List;
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
        return new ResponseEntity<>(this.statsService.getInstructorStats(principal.getName()),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats/monthly")
    public ResponseEntity<List<Map<String, Object>>> adminMonthlyStats(
            @RequestParam(name = "year") int year) {
        return new ResponseEntity<>(this.statsService.getMonthlyRevenue(year), HttpStatus.OK);
    }
    
}