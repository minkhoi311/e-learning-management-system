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
    public List<Enrollment> getByUsername(String username);
    public Enrollment enroll(int courseId, String username);
    public String createPaymentSession(int enrollmentId, String method, String username);
    public boolean processWebhook(Map<String, Object> payload);
}
