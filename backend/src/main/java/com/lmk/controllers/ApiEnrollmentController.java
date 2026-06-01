/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.pojo.Enrollment;
import com.lmk.services.CourseService;
import com.lmk.services.EnrollmentService;
import com.lmk.services.MomoService;
import com.lmk.services.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Acer
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiEnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MomoService moMoService;
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/secure/enrollments/check/{courseId}")
    public ResponseEntity<Object> checkEnrollment(@PathVariable("courseId") int courseId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        Enrollment e = this.enrollmentService.getByStudentAndCourse(
                this.userService.getUserByUsername(principal.getName()).getId(), courseId);

        if (e != null) {
            return new ResponseEntity<>(Map.of("enrollmentId", e.getId()), HttpStatus.OK);
        }

        return new ResponseEntity<>(Map.of("message", "Chưa đăng ký khóa học này!"), HttpStatus.NOT_FOUND);
    }

    // POST /api/courses/{courseId}/enroll — Student ghi danh
    // @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/secure/courses/{courseId}/enroll")
    public ResponseEntity<Object> enroll(@PathVariable("courseId") int courseId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }
        try {
            Enrollment e = this.enrollmentService.enroll(courseId, principal.getName());
            return new ResponseEntity<>(e, HttpStatus.CREATED);
        } catch (IllegalStateException ex) {
            // Đã đăng ký rồi
            return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException ex) {
            // Không tìm thấy user/course
            return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // GET /api/enrollments — Student xem danh sách khóa đã đăng ký
    // @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/secure/enrollments")
    public ResponseEntity<Object> myEnrollments(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        List<Enrollment> list = this.enrollmentService.getByUsername(principal.getName());

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/secure/enrollments/{enrollmentId}/pay")
    public ResponseEntity<Map<String, Object>> processPayment(
            @PathVariable("enrollmentId") int enrollmentId,
            @RequestBody Map<String, String> payload, 
            Principal principal) {
        
        String method = payload.get("method");
        
        // 1. Gọi Service để xử lý toàn bộ logic Database (Tạo Payment PENDING)
        Enrollment enrollment = this.enrollmentService.updatePaymentMethod(enrollmentId, method);
        
        if (enrollment == null) {
            return new ResponseEntity<>(Map.of("message", "Không tìm thấy lượt đăng ký!"), HttpStatus.NOT_FOUND);
        }

        // 2. Trả về kết quả tùy theo phương thức
        if ("CASH".equalsIgnoreCase(method)) {
            
            return new ResponseEntity<>(Map.of(
                    "message", "Đã ghi nhận phương thức tiền mặt. Vui lòng đến trung tâm thanh toán!"
            ), HttpStatus.OK);
            
        } else if ("MOMO".equalsIgnoreCase(method)) {
            
            // XỬ LÝ LỖI BIGDECIMAL Ở ĐÂY: Dùng .doubleValue() trước khi round
            long amount = Math.round(enrollment.getCourseId().getPrice().doubleValue());
            
            String orderInfo = "Thanh toan khoa hoc: " + enrollment.getCourseId().getSubject();
            
            String moMoPayUrl = this.moMoService.createMoMoPayment(enrollmentId, amount, orderInfo);
            
            if (moMoPayUrl != null) {
                return new ResponseEntity<>(Map.of(
                        "payment_url", moMoPayUrl,
                        "message", "Đang chuyển hướng sang MoMo..."
                ), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "Lỗi tạo link MoMo!"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(Map.of("message", "Phương thức thanh toán không hợp lệ"), HttpStatus.BAD_REQUEST);
    }

    // POST /api/payments/webhook — IPN callback từ cổng thanh toán (PUBLIC, bypass Security)
    @PostMapping("/payments/webhook")
    public ResponseEntity<Map<String, String>> webhook(@RequestBody Map<String, Object> payload) {
        boolean ok = this.enrollmentService.processWebhook(payload);

        if (!ok) {
            return new ResponseEntity<>(Map.of("message", "Payload không hợp lệ!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Map.of("message", "OK"), HttpStatus.OK);
    }
    
    
    @GetMapping("/secure/instructor/courses/{courseId}/students")
    public ResponseEntity<Object> getStudentsByCourse(@PathVariable("courseId") int courseId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        // 1. Kiểm tra giảng viên có sở hữu khóa học này không
        Course course = this.courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>(Map.of("message", "Khóa học không tồn tại!"), HttpStatus.NOT_FOUND);
        }
        
        if (!course.getInstructorId().getUsername().equals(principal.getName())) {
            return new ResponseEntity<>(Map.of("message", "Bạn không có quyền quản lý khóa học này!"), HttpStatus.FORBIDDEN);
        }

        // 2. Lấy danh sách sinh viên đã enroll
        List<Enrollment> enrollments = this.enrollmentService.getEnrollmentsByCourse(courseId);
        
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }
}
