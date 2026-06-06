package com.lmk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lmk.pojo.Course;
import com.lmk.pojo.User;
import com.lmk.repositories.CourseRepository;
import com.lmk.repositories.UserRepository;
import com.lmk.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired private CourseRepository courseRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private Cloudinary cloudinary;

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        return courseRepo.getCourses(params);
    }

    @Override
    public Course getCourseById(int id) {
        Course course = this.courseRepo.getCourseById(id);
        if (course == null) throw new ExceptionInInitializerError("Khóa học không tồn tại.");
        return course;
    }

    @Override
    public void addOrUpdateCourse(Course c) {
        if (c.getFile() != null && !c.getFile().isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(c.getFile().getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                c.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(CourseServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (c.getId() == null) c.setCreatedTime(new Date());
        else c.setUpdatedTime(new Date());

        this.courseRepo.addOrUpdateCourse(c);
    }

    @Override
    public void deleteCourse(int id) {
        this.courseRepo.deleteCourse(id);
    }

    @Override
    public Map<String, String> validate(Course c) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (c.getSubject() == null || c.getSubject().isBlank()) errors.put("subject", "Tên khóa học không được để trống.");
        if (c.getPrice() == null) errors.put("price", "Học phí không được để trống.");
        else if (c.getPrice() < 0) errors.put("price", "Học phí không được âm.");
        if (c.getInstructorId() == null) errors.put("instructor", "Khóa học phải có giảng viên.");
        return errors;
    }

    @Override
    public List<Course> getCoursesByIds(String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(String::trim).map(Integer::parseInt).collect(Collectors.toList());
        return this.courseRepo.getCoursesByIds(idList);
    }

    @Override
    public Course createCourse(Course c, String username) {
        User u = userRepo.getUserByUsername(username);
        c.setInstructorId(u);

        Map<String, String> errors = this.validate(c);
        if (!errors.isEmpty()) throw new IllegalArgumentException("Dữ liệu không hợp lệ: " + errors);

        this.addOrUpdateCourse(c);
        return c;
    }

    @Override
    public Course updateCourse(int courseId, Course c, String username) {
        Course existingCourse = this.getCourseById(courseId);
        if (existingCourse == null) throw new NoSuchElementException("Khóa học không tồn tại.");

        User u = userRepo.getUserByUsername(username);
        if (u.getRole().equals("INSTRUCTOR") && !existingCourse.getInstructorId().getUsername().equals(u.getUsername())) {
            throw new SecurityException("Không có quyền sửa khóa học này.");
        }

        existingCourse.setSubject(c.getSubject());
        existingCourse.setDescription(c.getDescription());
        existingCourse.setPrice(c.getPrice());
        existingCourse.setDurationHours(c.getDurationHours());
        existingCourse.setVideoUrl(c.getVideoUrl());

        if (c.getCategoryId() != null && c.getCategoryId().getId() != null) existingCourse.setCategoryId(c.getCategoryId());
        if (c.getFile() != null && !c.getFile().isEmpty()) existingCourse.setFile(c.getFile());

        Map<String, String> errors = this.validate(existingCourse);
        if (!errors.isEmpty()) throw new IllegalArgumentException("Dữ liệu không hợp lệ: " + errors);

        this.addOrUpdateCourse(existingCourse);
        return existingCourse;
    }
}