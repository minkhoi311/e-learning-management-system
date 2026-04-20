/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lmk.pojo.Course;
import com.lmk.repositories.CourseRepository;
import com.lmk.services.CourseService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Acer
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private Cloudinary cloudinary;

    public List<Course> getCourses(Map<String, String> params) {
        return courseRepo.getCourses(params);
    }

    public Course getCourseById(int id) {
        return courseRepo.getCourseById(id);
    }

    @Override
    public void addOrUpdateCourse(Course c) {
        if (!c.getFile().isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(c.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                c.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(CourseServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Map<String, String> errors = validate(c);
        if (!errors.isEmpty()) 
            return;
        
        this.courseRepo.addOrUpdateCourse(c);
    }

    @Override
    public boolean deleteCourse(int id) {
        return courseRepo.deleteCourse(id);
    }

    @Override
    public Long countCourse(Map<String, String> params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Map<String, String> validate(Course course) {
        Map<String, String> errors = new HashMap<>();

        // Kiểm tra tên khóa học
        if (course.getSubject() == null || course.getSubject().isBlank()) {
            errors.put("subject", "Tên khóa học không được để trống.");
        } else if (course.getSubject().length() > 255) {
            errors.put("subject", "Tên khóa học không được vượt quá 255 ký tự.");
        }

        // Kiểm tra học phí
        if (course.getPrice() == null) {
            errors.put("price", "Học phí không được để trống (nhập 0 nếu miễn phí).");
        } else if (course.getPrice() <  0) {
            errors.put("price", "Học phí không được là số âm.");
        }

        // Kiểm tra thời lượng
        if (course.getDurationHours() != null && course.getDurationHours() <= 0) {
            errors.put("durationHours", "Thời lượng phải lớn hơn 0 giờ.");
        }

        // Kiểm tra giảng viên
        if (course.getInstructorId() == null) {
            errors.put("instructor", "Vui lòng chọn giảng viên phụ trách.");
        }

        return errors;
    }
}
