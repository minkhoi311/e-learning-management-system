/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.services.CourseService;
import com.lmk.utils.DaoUtils;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private Environment env;

    //PUBLIC/api/courses?kw=&cateId=&fromPrice=&toPrice=&sort=&page=
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
    
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> detail(@PathVariable int courseId) {
        Course c = this.courseService.getCourseById(courseId);
        if (c == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @GetMapping("/courses/compare")
    public ResponseEntity<List<Course>> compare(@RequestParam String ids) {
        if (ids == null || ids.isBlank())
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        List<Course> result = this.courseService.getCoursesByIds(ids);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    //Instructor
    // @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/courses")
    public ResponseEntity<Object> create(@ModelAttribute Course c, Principal principal) {
        // TODO: gán instructorId từ principal khi có Security
        // User u = userService.getUserByUsername(principal.getName());
        // c.setInstructorId(u);

        Map<String, String> errors = this.courseService.validate(c);
        if (!errors.isEmpty())
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

        this.courseService.addOrUpdateCourse(c);
        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }
    
    // PATCH /api/courses/{id} — Instructor sửa khóa học của mình
    // @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PatchMapping("/courses/{courseId}")
    public ResponseEntity<Object> update(@PathVariable int courseId, @ModelAttribute Course c) {
        // TODO: kiểm tra quyền sở hữu khi có Security
        // if (!c.getInstructorId().getUsername().equals(principal.getName())) return 403

        c.setId(courseId);
        Map<String, String> errors = this.courseService.validate(c);
        if (!errors.isEmpty())
            return ResponseEntity.badRequest().body(errors);

        this.courseService.addOrUpdateCourse(c);
        return new ResponseEntity<>(c, HttpStatus.OK);
    }
    
    // DELETE /api/courses/{id} — Instructor xóa khóa học của mình
    // @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable int courseId) {
        // TODO: kiểm tra quyền sở hữu khi có Security
        boolean ok = this.courseService.deleteCourse(courseId);
        if (!ok)
            return new ResponseEntity<>(Map.of("message", "Không tìm thấy khóa học!"), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(Map.of("message", "Xóa khóa học thành công!"), HttpStatus.NO_CONTENT);
    }
}
