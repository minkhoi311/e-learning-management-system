/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.pojo.Lesson;
import com.lmk.pojo.LessonComment;
import com.lmk.services.CourseService;
import com.lmk.services.LessonCommentService;
import com.lmk.services.LessonService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class ApiLessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LessonCommentService commentService;

    @Autowired
    private Environment env;

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<Lesson> getLessonDetail(@PathVariable("lessonId") int lessonId) {
        Lesson l = this.lessonService.getLessonById(lessonId);

        if (l == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(l, HttpStatus.OK);
    }

    //public
    // GET /api/courses/{courseId}/lessons  — STUDENT (đã enroll), INSTRUCTOR, ADMIN
// @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<Lesson>> listByCourse(
            @PathVariable("courseId") int courseId,
            @RequestParam Map<String, String> params) {
        params.put("courseId", String.valueOf(courseId));
        params.put("isActive", "true");
        return new ResponseEntity<>(this.lessonService.getLessons(params), HttpStatus.OK);
    }

    @GetMapping("/lessons/{lessonId}/comments")
    public ResponseEntity<List<LessonComment>> listComments(@PathVariable("lessonId") int lessonId) {
        return new ResponseEntity<>(this.commentService.getByLesson(lessonId), HttpStatus.OK);
    }

    // @PreAuthorize("isAuthenticated()")
    @PostMapping("/secure/lessons/{lessonId}/comments")
    public ResponseEntity<Object> addComment(
            @PathVariable("lessonId") int lessonId,
            @RequestBody Map<String, Object> body,
            Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        String content = (String) body.get("content");
        Integer parentId = body.get("parent_comment_id") != null
                ? Integer.parseInt(body.get("parent_comment_id").toString()) : null;

        LessonComment saved = this.commentService.add(lessonId, content, parentId, principal.getName());
        if (saved == null) {
            return new ResponseEntity<>(Map.of("message", "Nội dung không được để trống!"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/secure/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable("commentId") int commentId,
            Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        boolean ok = this.commentService.delete(commentId, principal.getName());
        if (!ok) {
            return new ResponseEntity<>(Map.of("message", "Không tìm thấy hoặc bạn không có quyền xóa!"), HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(Map.of("message", "Xóa bình luận thành công!"), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<Object> createLesson(@PathVariable("courseId") int courseId,
            @ModelAttribute Lesson l) {
        // TODO: kiểm tra instructor có sở hữu course không khi có Security
        Course c = new Course();
        c.setId(courseId);
        l.setCourseId(c);

        this.lessonService.addOrUpdateLesson(l);
        return new ResponseEntity<>(l, HttpStatus.CREATED);
    }

    // PUT /api/lessons/{id} — Instructor sửa bài học
    // @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable("lessonId") int lessonId,
            @ModelAttribute Lesson l) {
        // TODO: kiểm tra quyền sở hữu khi có Security
        l.setId(lessonId);
        this.lessonService.addOrUpdateLesson(l);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }

    // DELETE /api/lessons/{id} — Instructor xóa bài học
    // @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Map<String, String>> deleteLesson(@PathVariable("lessonId") int lessonId) {
        // TODO: kiểm tra quyền sở hữu khi có Security
        this.lessonService.deleteLesson(lessonId);
        return new ResponseEntity<>(Map.of("message", "Xóa bài học thành công!"), HttpStatus.NO_CONTENT);
    }
}
