/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.Enrollment;
import com.lmk.services.EnrollmentService;
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
    
    // POST /api/courses/{courseId}/enroll — Student ghi danh
    // @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<Object> enroll(@PathVariable int courseId, Principal principal) {
        if (principal == null)
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
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
    @GetMapping("/enrollments")
    public ResponseEntity<Object> myEnrollments(Principal principal) {
        if (principal == null)
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);

        List<Enrollment> list = this.enrollmentService.getByUsername(principal.getName());

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    // POST /api/enrollments/{enrollmentId}/pay — Tạo phiên thanh toán
    // @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/enrollments/{enrollmentId}/pay")
    public ResponseEntity<Object> pay(@PathVariable int enrollmentId, @RequestBody Map<String, String> body, Principal principal) {
        if (principal == null)
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);

        try {
            String method = body.getOrDefault("method", "MOMO");
            String url = this.enrollmentService.createPaymentSession(enrollmentId, method, principal.getName());

            return new ResponseEntity<>(Map.of("payment_url", url), HttpStatus.OK);

        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // POST /api/payments/webhook — IPN callback từ cổng thanh toán (PUBLIC, bypass Security)
    @PostMapping("/payments/webhook")
    public ResponseEntity<Map<String, String>> webhook(@RequestBody Map<String, Object> payload) {
        boolean ok = this.enrollmentService.processWebhook(payload);

        if (!ok)
            return new ResponseEntity<>(Map.of("message", "Payload không hợp lệ!"), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(Map.of("message", "OK"), HttpStatus.OK);
    }
}
