/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.pojo.User;
import com.lmk.services.CourseService;
import com.lmk.services.UserService;
import com.lmk.utils.DaoUtils;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Acer
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> list(@RequestParam Map<String, String> params) {
        List<Course> courses = this.courseService.getCourses(params);
        Long totalItems = this.courseService.countCourse(params);
        int pageSize = Integer.parseInt(env.getProperty("courses.page_size", "20"));
        int totalPages = DaoUtils.calculateTotalPages(totalItems, pageSize);
        int currentPage = Integer.parseInt(params.getOrDefault("page", "1"));

        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", currentPage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/courses/compare")
    public ResponseEntity<List<Course>> compare(@RequestParam("ids") String ids) {
        if (ids == null || ids.isBlank()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        List<Course> result = this.courseService.getCoursesByIds(ids);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> detail(@PathVariable("courseId") int courseId) {
        Course c = this.courseService.getCourseById(courseId);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PostMapping("/secure/courses")
    public ResponseEntity<Object> create(@ModelAttribute Course c, Principal principal) {
        User u = userService.getUserByUsername(principal.getName());
        c.setInstructorId(u);

        Map<String, String> errors = this.courseService.validate(c);
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        this.courseService.addOrUpdateCourse(c);
        return new ResponseEntity<>(Map.of("message", "Tạo khóa học thành công!"), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PatchMapping("/secure/courses/{courseId}")
    public ResponseEntity<Object> update(@PathVariable("courseId") int courseId, @ModelAttribute Course c, Principal principal) {

        // 1. KIỂM TRA QUYỀN SỞ HỮU
        Course existingCourse = this.courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return new ResponseEntity<>(Map.of("message", "Khóa học không tồn tại!"), HttpStatus.NOT_FOUND);
        }

        // Nếu người đang đăng nhập KHÔNG PHẢI là chủ khóa học (và cũng không phải ADMIN) -> Đuổi ra
        User u = userService.getUserByUsername(principal.getName());
        if (u.getRole().equals("INSTRUCTOR") && !existingCourse.getInstructorId().getUsername().equals(u.getUsername())) {
            return new ResponseEntity<>(Map.of("message", "CẢNH BÁO: Bạn không có quyền sửa khóa học của người khác!"), HttpStatus.FORBIDDEN);
        }

        c.setId(courseId);
        c.setInstructorId(existingCourse.getInstructorId()); // Giữ nguyên chủ sở hữu gốc

        Map<String, String> errors = this.courseService.validate(c);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        this.courseService.addOrUpdateCourse(c);
        return new ResponseEntity<>(Map.of("message", "Cập nhật khóa học thành công!"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/secure/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "courseId") int courseId, Principal principal) {
        this.courseService.deleteCourse(courseId);
//        Course existingCourse = this.courseService.getCourseById(courseId);
//        if (existingCourse != null) {
//            User u = userService.getUserByUsername(principal.getName());
//
//            if (u.getRole().equals("INSTRUCTOR") && !existingCourse.getInstructorId().getUsername().equals(u.getUsername())) {
//                return new ResponseEntity<>(Map.of("message", "CẢNH BÁO: Bạn không có quyền xóa khóa học của người khác!"), HttpStatus.FORBIDDEN);
//            }
//        }
//
//        this.courseService.deleteCourse(courseId);
//        return new ResponseEntity<>(Map.of("message", "Đã xóa khóa học thành công!"), HttpStatus.OK);
//    }
    }

}
