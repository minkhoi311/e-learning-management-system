/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.services;

import com.lmk.pojo.Enrollment;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Acer
 */
public interface EnrollmentService {
    List<Enrollment> getByUsername(String username);
    Enrollment enroll(int courseId, String username);
    Enrollment getByStudentAndCourse(int studentId, int courseId);
    void markLessonAsCompleted(int enrollmentId, int lessonId);
    String createPaymentSession(int enrollmentId, String method, String username);
    Enrollment updatePaymentMethod(int enrollmentId, String method);
    boolean processWebhook(Map<String, Object> payload);

    List<Enrollment> getEnrollmentsByCourse(int courseId);
}
