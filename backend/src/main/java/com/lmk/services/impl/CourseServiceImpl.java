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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        return courseRepo.getCourses(params);
    }
    
    @Override
    public Long countCourses(Map<String, String> params) {
        return this.courseRepo.countCourses(params);
    }

    @Override
    public Course getCourseById(int id) {
        return courseRepo.getCourseById(id);
    }

    @Override
    public void addOrUpdateCourse(Course c) {
        if (c.getFile() != null && !c.getFile().isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(
                    c.getFile().getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                c.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(CourseServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (c.getId() == null) {
            c.setCreatedTime(new Date());
        } else {
            c.setUpdatedTime(new Date());
        }

        this.courseRepo.addOrUpdateCourse(c);
    }

    @Override
    public void deleteCourse(int id) {
        this.courseRepo.deleteCourse(id);
    }

    @Override
    public Map<String, String> validate(Course c) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (c.getSubject() == null || c.getSubject().isBlank())
            errors.put("subject", "Tên khóa học không được để trống.");
        if (c.getPrice() == null)
            errors.put("price", "Học phí không được để trống.");
        else if (c.getPrice() < 0)
            errors.put("price", "Học phí không được âm.");
        if (c.getInstructorId() == null)
            errors.put("instructor", "Khóa học phải có giảng viên.");
        return errors;
    }

    @Override
    public List<Course> getCoursesByIds(String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return this.courseRepo.getCoursesByIds(idList);
    }
}
