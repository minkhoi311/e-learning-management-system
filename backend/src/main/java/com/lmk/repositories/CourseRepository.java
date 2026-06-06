/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.Course;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Acer
 */
public interface CourseRepository {
    List<Course> getCourses(Map<String, String> params);
    void addOrUpdateCourse(Course c);
    Course getCourseById(int id);
    List<Course> getCoursesByIds(List<Integer> ids);
    void deleteCourse(int id);
}
