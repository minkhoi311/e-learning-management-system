package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> list(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(this.courseService.getCourses(params));
    }

    @GetMapping("/courses/compare")
    public ResponseEntity<List<Course>> compare(@RequestParam("ids") String ids) {
        if (ids == null || ids.isBlank()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(this.courseService.getCoursesByIds(ids), HttpStatus.OK);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<Course> detail(@PathVariable("courseId") int courseId) {
        try {
            return new ResponseEntity<>(this.courseService.getCourseById(courseId), HttpStatus.OK);
        } catch (ExceptionInInitializerError e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PostMapping("/secure/courses")
    public ResponseEntity<Course> create(@ModelAttribute Course c, Principal principal) {
        try {
            Course created = this.courseService.createCourse(c, principal.getName());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PatchMapping("/secure/courses/{courseId}")
    public ResponseEntity<Course> update(@PathVariable("courseId") int courseId, @ModelAttribute Course c, Principal principal) {
        try {
            Course updated = this.courseService.updateCourse(courseId, c, principal.getName());
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable(value = "courseId") int courseId) {
        this.courseService.deleteCourse(courseId);
    }
}