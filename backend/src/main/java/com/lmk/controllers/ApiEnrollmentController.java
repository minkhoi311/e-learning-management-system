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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiEnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/secure/enrollments/check/{courseId}")
    public ResponseEntity<Enrollment> checkEnrollment(@PathVariable("courseId") int courseId, Principal principal) {
        Enrollment e = this.enrollmentService.getByStudentAndCourse(
 this.userService.getUserByUsername(principal.getName()).getId(), courseId);
        return new ResponseEntity<>(e, HttpStatus.OK);
    }

    @PostMapping("/secure/courses/{courseId}/enroll")
    public ResponseEntity<Enrollment> enroll(@PathVariable("courseId") int courseId, Principal principal) {
        Enrollment e = this.enrollmentService.enroll(courseId, principal.getName());
        return new ResponseEntity<>(e, HttpStatus.CREATED);
    }

    @GetMapping("/secure/enrollments")
    public ResponseEntity<List<Enrollment>> myEnrollments(Principal principal) {
        List<Enrollment> list = this.enrollmentService.getByUsername(principal.getName());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/secure/enrollments/{enrollmentId}/pay")
    public ResponseEntity<Map<String, Object>> processPayment(
            @PathVariable("enrollmentId") int enrollmentId,
            @RequestBody Map<String, String> payload) {

        String method = payload.get("method");
        Map<String, Object> response = this.enrollmentService.processPaymentFlow(enrollmentId, method);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        boolean ok = this.enrollmentService.processWebhook(payload);
        if (!ok) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/secure/instructor/courses/{courseId}/students")
    public ResponseEntity<List<Enrollment>> getStudentsByCourse(@PathVariable("courseId") int courseId, Principal principal) {
        List<Enrollment> enrollments = this.enrollmentService.getEnrollmentsByCourse(courseId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }
}
