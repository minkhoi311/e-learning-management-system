/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.lmk.pojo.Course;
import com.lmk.pojo.Enrollment;
import com.lmk.pojo.User;
import com.lmk.repositories.CourseRepository;
import com.lmk.repositories.EnrollmentRepository;
import com.lmk.repositories.UserRepository;
import com.lmk.services.EnrollmentService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Acer
 */
public class EnrollmentServiceImpl implements EnrollmentService{
    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CourseRepository courseRepo;
    
    @Override
    public List<Enrollment> getByUsername(String username) {
        User u = this.userRepo.getUserByUsername(username);
        if (u == null) return List.of();
        return this.enrollmentRepo.getByStudent(u.getId());
    }

    @Override
    public Enrollment enroll(int courseId, String username) {
        User u = this.userRepo.getUserByUsername(username);
        Course c = this.courseRepo.getCourseById(courseId);
        
        if (u == null || c == null)
            throw new IllegalArgumentException("Không tìm thấy người dùng hoặc khóa học!");
        Enrollment existing = this.enrollmentRepo.getByStudentAndCourse(u.getId(), courseId);
        if (existing != null)
            throw new IllegalStateException("Bạn đã đăng ký khóa học này rồi!");
        
        Enrollment e = new Enrollment();
        e.setStudentId(u);
        e.setCourseId(c);
        e.setProgressPercent(0.0);
        e.setEnrolledTime(new Date());
        return this.enrollmentRepo.enroll(e);
    }

    @Override
    public String createPaymentSession(int enrollmentId, String method, String username) {
//        Enrollment e = this.enrollmentRepo.getById(enrollmentId);
//        if (e == null)
//            throw new IllegalArgumentException("Không tìm thấy đăng ký học!");
//        if (!e.getStudentId().getUsername().equals(username))
//            throw new SecurityException("Bạn không có quyền thanh toán enrollment này!");
//
//        // TODO: Tích hợp Momo / ZaloPay thực tế ở đây
//        return "https://payment.example.com/pay?enrollmentId=" + enrollmentId + "&method=" + method;
          return null;
    }

    @Override
    public boolean processWebhook(Map<String, Object> payload) {
//                // TODO: Xác thực chữ ký từ cổng thanh toán, cập nhật Payment status
//        String txRef = (String) payload.get("transaction_id");
//        String status = (String) payload.get("status");
//        return txRef != null && status != null;
            return false;
    }
    
}
