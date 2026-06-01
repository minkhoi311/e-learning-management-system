/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.services.EnrollmentService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Acer
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiProgressController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/secure/enrollments/{enrollmentId}/lessons/{lessonId}/complete")
    public ResponseEntity<Map<String, String>> completeLesson(
            @PathVariable("enrollmentId") int enrollmentId,
            @PathVariable("lessonId") int lessonId,
            Principal principal) {

        try {
            this.enrollmentService.markLessonAsCompleted(enrollmentId, lessonId);
            return new ResponseEntity<>(Map.of("message", "Đã lưu tiến độ!"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Lỗi: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
