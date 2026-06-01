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
        return this.enrollmentRepo.getByStudent(u.getId());
    }

    @Override
    public Enrollment enroll(int courseId, String username) {
        User u = this.userRepo.getUserByUsername(username);
        Course c = this.courseRepo.getCourseById(courseId);

        if (u == null || c == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng hoặc khóa học!");
        }
        Enrollment existing = this.enrollmentRepo.getByStudentAndCourse(u.getId(), courseId);
        if (existing != null) {
            throw new IllegalStateException("Bạn đã đăng ký khóa học này rồi!");
        }

        Enrollment e = new Enrollment();
        e.setStudentId(u);
        e.setCourseId(c);
        e.setProgressPercent(0.0);
        e.setEnrolledTime(new Date());
        return this.enrollmentRepo.enroll(e);
    }

    @Override
    public void markLessonAsCompleted(int enrollmentId, int lessonId) {
        LessonProgress lp = new LessonProgress();
        lp.setEnrollmentId(enrollmentRepo.getById(enrollmentId));
        lp.setLessonId(lessonRepo.getLessonById(lessonId));
        lp.setIsCompleted(true);
        lp.setCompletedTime(new Date());
        lessonProgressRepo.save(lp);

        Enrollment e = enrollmentRepo.getById(enrollmentId);
        long totalLessons = lessonRepo.countLessonByCourseId(e.getCourseId().getId());
        long completed = lessonProgressRepo.countCompletedLessons(enrollmentId);

        double progress = (double) completed / totalLessons * 100;

        e.setProgressPercent(progress);
        enrollmentRepo.update(e);
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
        try {
            // 1. Lấy trạng thái giao dịch từ MoMo (0 là thành công)
            Object resultCodeObj = payload.get("resultCode");
            if (resultCodeObj == null) {
                return false;
            }
            int resultCode = Integer.parseInt(resultCodeObj.toString());

            // 2. Lấy mã đơn hàng (orderId)
            String orderIdMoMo = (String) payload.get("orderId");
            if (orderIdMoMo == null) {
                return false;
            }

            // Nếu giao dịch thất bại/hủy -> Vẫn báo true để MoMo không gọi lại nữa
            if (resultCode != 0) {
                System.out.println("Giao dịch MoMo thất bại hoặc bị hủy. Mã lỗi: " + resultCode);
                return true;
            }

            // 3. Tách orderIdMoMo (VD: "15_uuid123") để lấy ID của Enrollment
            String[] parts = orderIdMoMo.split("_");
            int enrollmentId = Integer.parseInt(parts[0]);

            // 4. Tìm Enrollment và Payment
            Enrollment enrollment = this.enrollmentRepo.getById(enrollmentId);

            if (enrollment != null && enrollment.getPayment() != null) {
                Payment p = enrollment.getPayment();

                // Nếu chưa SUCCESS thì mới cập nhật để tránh lặp
                if (!"SUCCESS".equals(p.getStatus())) {
                    p.setStatus("SUCCESS");
                    p.setPaidTime(new Date()); // Ghi nhận thời gian thanh toán

                    // Lưu luôn mã giao dịch của MoMo (transId) để đối soát
                    if (payload.get("transId") != null) {
                        p.setTransactionReference(payload.get("transId").toString());
                    }

                    this.enrollmentRepo.update(enrollment);
                    System.out.println("🎉 MoMo IPN: Đã kích hoạt thành công khóa học cho Enrollment ID: " + enrollmentId);
                }
            } else {
                System.out.println("⚠️ MoMo IPN: Đơn hàng không tồn tại hoặc chưa có hóa đơn Payment.");
            }

            return true;

        } catch (Exception e) {
            System.err.println("Lỗi xử lý webhook MoMo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Enrollment getByStudentAndCourse(int studentId, int courseId) {
        return this.enrollmentRepo.getByStudentAndCourse(studentId, courseId);
    }

    @Override
    public Enrollment updatePaymentMethod(int enrollmentId, String method) {
        Enrollment enrollment = this.enrollmentRepo.getById(enrollmentId);
        if (enrollment != null) {
            Payment p = enrollment.getPayment();
            if (p == null) {
                p = new Payment();
                p.setEnrollmentId(enrollment); 
                enrollment.setPayment(p);
            }
            p.setPaymentMethod(method);
            p.setAmount(enrollment.getCourseId().getPrice());
            if ("CASH".equalsIgnoreCase(method)) {
                p.setStatus("SUCCESS"); 
                p.setPaidTime(new Date());
            } else {
                p.setStatus("PENDING"); 
            }
            enrollment.setPayment(p);

            this.enrollmentRepo.update(enrollment);
            if (p.getPaidTime() == null) {
                p.setPaidTime(new Date());
            }
            
            Enrollment updatedEnrollment = this.enrollmentRepo.update(enrollment);
            return updatedEnrollment;
        }

        return null;
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return this.enrollmentRepo.findByCourseId(courseId);
    }
}
