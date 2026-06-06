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
        return stats;
    }

    @Override
    public List<Map<String, Object>> getMonthlyRevenue(int year) {
        double[] revenues = new double[13];
        for (Object[] row : statsRepo.getMonthlyRevenue(year)) {
            int month = ((Number) row[0]).intValue();
            revenues[month] = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(Map.of("month", m, "revenue", revenues[m]));
        }

        return result;
    }
}
