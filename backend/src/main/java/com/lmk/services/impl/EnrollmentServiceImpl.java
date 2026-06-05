/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.lmk.pojo.Course;
import com.lmk.pojo.Enrollment;
import com.lmk.pojo.LessonProgress;
import com.lmk.pojo.Payment;
import com.lmk.pojo.User;
import com.lmk.repositories.CourseRepository;
import com.lmk.repositories.EnrollmentRepository;
import com.lmk.repositories.LessonProgressRepository;
import com.lmk.repositories.LessonRepository;
import com.lmk.repositories.UserRepository;
import com.lmk.services.EnrollmentService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */
@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private LessonProgressRepository lessonProgressRepo;

    @Override
    public List<Enrollment> getByUsername(String username) {
        User u = this.userRepo.getUserByUsername(username);
        if (u == null) {
            return List.of();
        }
        return this.enrollmentRepo.getByStudentId(u.getId());
    }

    @Override
    public Enrollment getByStudentAndCourse(int studentId, int courseId) {
        return this.enrollmentRepo.getByStudentAndCourse(studentId, courseId);
    }

    @Override
    public Enrollment enroll(int courseId, String username) {
        User u = this.userRepo.getUserByUsername(username);
        Course c = this.courseRepo.getCourseById(courseId);

        if (u == null || c == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng hoặc khóa học!");
        }

        if (this.enrollmentRepo.getByStudentAndCourse(u.getId(), courseId) != null) {
            throw new IllegalStateException("Bạn đã đăng ký khóa học này rồi!");
        }

        Enrollment e = new Enrollment();
        e.setStudentId(u);
        e.setCourseId(c);
        e.setProgressPercent(0.0);
        e.setEnrolledTime(new Date());
        this.enrollmentRepo.addOrUpdateEnrollment(e);
        return e;
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return this.enrollmentRepo.getByCourseId(courseId);
    }

    @Override
    public void markLessonCompleted(int enrollmentId, int lessonId) {
        Enrollment e = this.enrollmentRepo.getById(enrollmentId);
        if (e == null) {
            throw new IllegalArgumentException("Không tìm thấy enrollment!");
        }

        // 1. THÊM LOGIC KIỂM TRA TRÙNG LẶP: Đã hoàn thành rồi thì thoát luôn
        boolean isAlreadyCompleted = e.getLessonProgressSet().stream()
                .anyMatch(lp -> lp.getLessonId().getId() == lessonId && lp.getIsCompleted());

        if (isAlreadyCompleted) {
            return; 
        }
        LessonProgress lp = new LessonProgress();
        lp.setEnrollmentId(e);
        lp.setLessonId(this.lessonRepo.getLessonById(lessonId));
        lp.setIsCompleted(true);
        lp.setCompletedTime(new Date());
        this.lessonProgressRepo.saveProgress(lp);

        long total = this.lessonRepo.countLessonsByCourseId(e.getCourseId().getId());
        long done = this.lessonProgressRepo.countCompletedLessons(enrollmentId);
        
        double percent = (total > 0) ? ((double) done / total * 100) : 0;
        e.setProgressPercent(Math.min(percent, 100.0));
        
        this.enrollmentRepo.addOrUpdateEnrollment(e);
    }
    @Override
    public Enrollment updatePaymentMethod(int enrollmentId, String method) {
        Enrollment e = this.enrollmentRepo.getById(enrollmentId);
        if (e == null) {
            return null;
        }

        Payment p = e.getPayment();
        if (p == null) {
            p = new Payment();
            p.setEnrollmentId(e);
            e.setPayment(p);
        }

        p.setPaymentMethod(method.toUpperCase());
        p.setAmount(e.getCourseId().getPrice());

        if ("CASH".equalsIgnoreCase(method)) {
            p.setStatus("SUCCESS");
            p.setPaidTime(new Date());
        } else {
            p.setStatus("PENDING");
        }

        this.enrollmentRepo.addOrUpdateEnrollment(e);
        return e;
    }
    
    
    @Override
    public boolean processWebhook(Map<String, Object> payload) {
        try {
            Object resultCodeObj = payload.get("resultCode");
            if (resultCodeObj == null) return false;

            int resultCode = Integer.parseInt(resultCodeObj.toString());
            String orderIdMoMo = (String) payload.get("orderId");
            if (orderIdMoMo == null) return false;

            if (resultCode != 0) return true; // Thất bại, báo OK để MoMo không gọi lại

            int enrollmentId = Integer.parseInt(orderIdMoMo.split("_")[0]);
            Enrollment e = this.enrollmentRepo.getById(enrollmentId);

            if (e != null && e.getPayment() != null && !"SUCCESS".equals(e.getPayment().getStatus())) {
                Payment p = e.getPayment();
                p.setStatus("SUCCESS");
                p.setPaidTime(new Date());
                if (payload.get("transId") != null)
                    p.setTransactionReference(payload.get("transId").toString());
                this.enrollmentRepo.addOrUpdateEnrollment(e);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
