package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.pojo.User;
import com.lmk.services.CourseService;
import com.lmk.services.UserService;
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
        int pageSize = Integer.parseInt(env.getProperty("courses.page_size", "20"));
        int currentPage = Integer.parseInt(params.getOrDefault("page", "1"));
        long totalItems = this.courseService.countCourses(params);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        Map<String, Object> res = new HashMap<>();
        res.put("courses", this.courseService.getCourses(params));
        res.put("totalItems", totalItems);
        res.put("totalPages", totalPages);
        res.put("currentPage", currentPage);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/courses/compare")
    public ResponseEntity<List<Course>> compare(@RequestParam("ids") String ids) {
        if (ids == null || ids.isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Course> create(@ModelAttribute Course c, Principal principal) {
        User u = userService.getUserByUsername(principal.getName());
        c.setInstructorId(u);

        Map<String, String> errors = this.courseService.validate(c);
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.courseService.addOrUpdateCourse(c);
        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PatchMapping("/secure/courses/{courseId}")
    public ResponseEntity<Course> update(@PathVariable("courseId") int courseId, @ModelAttribute Course c, Principal principal) {
        Course existingCourse = this.courseService.getCourseById(courseId);
        if (existingCourse == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User u = userService.getUserByUsername(principal.getName());
        if (u.getRole().equals("INSTRUCTOR") && !existingCourse.getInstructorId().getUsername().equals(u.getUsername())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        existingCourse.setSubject(c.getSubject());
        existingCourse.setDescription(c.getDescription());
        existingCourse.setPrice(c.getPrice());
        existingCourse.setDurationHours(c.getDurationHours());
        existingCourse.setVideoUrl(c.getVideoUrl());

        if (c.getCategoryId() != null && c.getCategoryId().getId() != null) {
            existingCourse.setCategoryId(c.getCategoryId());
        }

        if (c.getFile() != null && !c.getFile().isEmpty()) {
            existingCourse.setFile(c.getFile());
        }
        Map<String, String> errors = this.courseService.validate(existingCourse);
        if (!errors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.courseService.addOrUpdateCourse(existingCourse);
        return new ResponseEntity<>(existingCourse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "courseId") int courseId, Principal principal) {
        this.courseService.deleteCourse(courseId);
    }
}
