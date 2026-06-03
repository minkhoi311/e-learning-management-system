package com.lmk.services.impl;

import com.lmk.pojo.User;
import com.lmk.repositories.StatsRepository;
import com.lmk.repositories.UserRepository;
import com.lmk.services.StatsService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsRepository statsRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCourses", statsRepo.countAllCourses());
        stats.put("totalEnrollments", statsRepo.countAllEnrollments());
        stats.put("totalRevenue", statsRepo.sumSuccessPayments());
        stats.put("usersByRole", statsRepo.countUsersByRole().stream()
            .map(r -> Map.of("role", r[0], "count", r[1])).toList());
        
        return stats;
    }

    @Override
    public Map<String, Object> getInstructorStats(String username) {
        User u = this.userRepo.getUserByUsername(username);
        List<Object[]> perCourse = statsRepo.getRevenuePerCourseByInstructor(u.getId());

        double totalRevenue = perCourse.stream()
            .mapToDouble(r -> r[2] != null ? ((Number) r[2]).doubleValue() : 0).sum();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCourses", statsRepo.countCoursesByInstructor(u.getId()));
        stats.put("totalEnrollments", statsRepo.countEnrollmentsByInstructor(u.getId()));
        stats.put("totalRevenue", totalRevenue);
        stats.put("revenuePerCourse", perCourse.stream()
            .map(r -> Map.of("subject", String.valueOf(r[0]), "enrollments", r[1],
                "revenue", r[2] != null ? r[2] : 0)).toList());
                
        return stats;
    }
}