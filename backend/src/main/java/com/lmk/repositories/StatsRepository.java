/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.repositories;
import java.util.List;
/**
 *
 * @author Acer
 */
public interface StatsRepository {
Long countAllCourses();
        Long countAllEnrollments();
        Double sumSuccessPayments();
        List<Object[]> countUsersByRole();
        Long countCoursesByInstructor(int instructorId);
        Long countEnrollmentsByInstructor(int instructorId);
        List<Object[]> getRevenuePerCourseByInstructor(int instructorId);
}
