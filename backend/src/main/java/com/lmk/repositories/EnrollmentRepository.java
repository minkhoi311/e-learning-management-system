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
    public List<Enrollment> getByStudent(int studentId);
    public Enrollment getById(int id);
    public Enrollment getByStudentAndCourse(int studentId, int courseId);
    public Enrollment enroll(Enrollment e);
    public Enrollment update(Enrollment e);
}
