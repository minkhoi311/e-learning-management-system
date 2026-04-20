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
    Course getCourseById(int id);
    public void addOrUpdateCourse(Course c);
    public boolean deleteCourse(int id);
    public Long countCourse(Map<String, String> params);
    
}
