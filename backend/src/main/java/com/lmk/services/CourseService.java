/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.services;

import com.lmk.pojo.Course;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Acer
 */
public interface CourseService {
    List<Course> getCourses(Map<String, String> params);
    Long countCourses(Map<String, String> params);
    Course getCourseById(int id);
    List<Course> getCoursesByIds(String ids);
    Map<String, String> validate(Course c);
    void addOrUpdateCourse(Course c);
    void deleteCourse(int id);
}
