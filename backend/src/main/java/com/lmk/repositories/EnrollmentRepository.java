/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.Enrollment;
import java.util.List;

/**
 *
 * @author Acer
 */
public interface EnrollmentRepository {
    Enrollment getById(int id);
    Enrollment getByStudentAndCourse(int studentId, int courseId);
    List<Enrollment> getByStudentId(int studentId);
    List<Enrollment> getByCourseId(int courseId);
    void addOrUpdateEnrollment(Enrollment e);
}
