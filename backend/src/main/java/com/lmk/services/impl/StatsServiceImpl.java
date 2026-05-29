/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.lmk.pojo.User;
import com.lmk.repositories.UserRepository;
import com.lmk.services.StatsService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */

@Service
@Transactional
public class StatsServiceImpl implements StatsService {
    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private UserRepository userRepo;
 
    @Override
    public Map<String, Object> getInstructorStats(String username) {
        User u = this.userRepo.getUserByUsername(username);
        Session session = this.factory.getObject().getCurrentSession();

        // Tổng số khóa học của instructor
        Query<Long> totalCoursesQ = session.createQuery(
                "SELECT COUNT(c) FROM Course c WHERE c.instructorId.id = :id", Long.class);
        totalCoursesQ.setParameter("id", u.getId());

        // Tổng học viên đã enroll vào các khóa của instructor
        Query<Long> totalEnrollQ = session.createQuery(
                "SELECT COUNT(e) FROM Enrollment e WHERE e.courseId.instructorId.id = :id", Long.class);
        totalEnrollQ.setParameter("id", u.getId());

        // Doanh thu từng khóa học (subject, số enroll, tổng tiền)
        Query<Object[]> perCourseQ = session.createQuery(
                "SELECT c.subject, COUNT(e), COALESCE(SUM(p.amount), 0) " +
                "FROM Course c " +
                "LEFT JOIN Enrollment e ON e.courseId.id = c.id " +
                "LEFT JOIN Payment p ON p.enrollmentId.id = e.id AND p.status = 'SUCCESS' " +
                "WHERE c.instructorId.id = :id " +
                "GROUP BY c.id, c.subject",
                Object[].class);
        perCourseQ.setParameter("id", u.getId());

        List<Object[]> rows = perCourseQ.getResultList();

        // Tổng doanh thu
        double totalRevenue = rows.stream()
                .mapToDouble(r -> r[2] != null ? ((Number) r[2]).doubleValue() : 0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", totalCoursesQ.uniqueResult());
        stats.put("totalEnrollments", totalEnrollQ.uniqueResult());
        stats.put("totalRevenue", totalRevenue);
        stats.put("revenuePerCourse", rows.stream().map(r -> Map.of(
                "subject",     String.valueOf(r[0]),
                "enrollments", r[1],
                "revenue",     r[2] != null ? r[2] : 0
        )).toList());

        return stats;
    }

    @Override
    public Map<String, Object> getAdminStats() {
        Session session = this.factory.getObject().getCurrentSession();

        // Tổng khóa học
        Long totalCourses = session.createQuery(
                "SELECT COUNT(c) FROM Course c", Long.class).uniqueResult();

        // Tổng lượt đăng ký
        Long totalEnrollments = session.createQuery(
                "SELECT COUNT(e) FROM Enrollment e", Long.class).uniqueResult();

        // Tổng doanh thu đã thanh toán thành công
        Double totalRevenue = session.createQuery(
                "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'",
                Double.class).uniqueResult();

        // Số lượng user theo vai trò
        List<Object[]> userByRole = session.createQuery(
                "SELECT u.role, COUNT(u) FROM User u GROUP BY u.role",
                Object[].class).getResultList();

        // Doanh thu theo tháng (6 tháng gần nhất)
        Query<Object[]> monthlyQ = session.createQuery(
                "SELECT MONTH(p.paidTime), YEAR(p.paidTime), SUM(p.amount) " +
                "FROM Payment p WHERE p.status = 'SUCCESS' " +
                "GROUP BY YEAR(p.paidTime), MONTH(p.paidTime) " +
                "ORDER BY YEAR(p.paidTime) DESC, MONTH(p.paidTime) DESC",
                Object[].class);
        monthlyQ.setMaxResults(6);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", totalCourses);
        stats.put("totalEnrollments", totalEnrollments);
        stats.put("totalRevenue", totalRevenue);
        stats.put("usersByRole", userByRole.stream().map(r -> Map.of(
                "role", String.valueOf(r[0]), "count", r[1]
        )).toList());
        stats.put("monthlyRevenue", monthlyQ.getResultList().stream().map(r -> Map.of(
                "month", r[0], "year", r[1], "revenue", r[2]
        )).toList());

        return stats;
    }
    
}
