package com.lmk.controllers;

import com.lmk.services.EnrollmentService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiProgressController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/secure/enrollments/{enrollmentId}/lessons/{lessonId}/complete")
    public ResponseEntity<Void> completeLesson(
            @PathVariable("enrollmentId") int enrollmentId,
            @PathVariable("lessonId") int lessonId,
            Principal principal) {
        try {
            this.enrollmentService.markLessonCompleted(enrollmentId, lessonId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}