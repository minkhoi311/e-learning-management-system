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
    
    @Autowired
    private MomoService moMoService;
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/secure/enrollments/check/{courseId}")
    public ResponseEntity<Enrollment> checkEnrollment(@PathVariable("courseId") int courseId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Enrollment e = this.enrollmentService.getByStudentAndCourse(
                this.userService.getUserByUsername(principal.getName()).getId(), courseId);

        if (e != null) {
            return new ResponseEntity<>(e, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/secure/courses/{courseId}/enroll")
    public ResponseEntity<Enrollment> enroll(@PathVariable("courseId") int courseId, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Enrollment e = this.enrollmentService.enroll(courseId, principal.getName());
            return new ResponseEntity<>(e, HttpStatus.CREATED);
        } catch (IllegalStateException ex) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/secure/enrollments")
    public ResponseEntity<List<Enrollment>> myEnrollments(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Enrollment> list = this.enrollmentService.getByUsername(principal.getName());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/secure/enrollments/{enrollmentId}/pay")
    public ResponseEntity<Map<String, Object>> processPayment(
            @PathVariable("enrollmentId") int enrollmentId,
            @RequestBody Map<String, String> payload, 
            Principal principal) {
        
        String method = payload.get("method");
        Enrollment enrollment = this.enrollmentService.updatePaymentMethod(enrollmentId, method);
        
        if (enrollment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if ("CASH".equalsIgnoreCase(method)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else if ("MOMO".equalsIgnoreCase(method)) {
            long amount = Math.round(enrollment.getCourseId().getPrice().doubleValue());
            String orderInfo = "Thanh toan khoa hoc: " + enrollment.getCourseId().getSubject();
            
            String moMoPayUrl = this.moMoService.createMoMoPayment(enrollmentId, amount, orderInfo);
            
            if (moMoPayUrl != null) {
                return new ResponseEntity<>(Map.of("payment_url", moMoPayUrl), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Course course = this.courseService.getCourseById(courseId);
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        if (!course.getInstructorId().getUsername().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<Enrollment> enrollments = this.enrollmentService.getEnrollmentsByCourse(courseId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }
}