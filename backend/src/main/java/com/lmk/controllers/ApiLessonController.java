package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.pojo.Lesson;
import com.lmk.pojo.LessonComment;
import com.lmk.services.CourseService;
import com.lmk.services.LessonCommentService;
import com.lmk.services.LessonService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<Lesson> getLessonDetail(@PathVariable("lessonId") int lessonId) {
        Lesson l = this.lessonService.getLessonById(lessonId);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }

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

    @PostMapping("/secure/lessons/{lessonId}/comments")
    public ResponseEntity<LessonComment> addComment( @PathVariable("lessonId") int lessonId,@RequestBody Map<String, Object> body,Principal principal) {
        String content = (String) body.get("content");
        LessonComment saved = this.commentService.addComment(lessonId, content, principal.getName());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/secure/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("commentId") int commentId,
            Principal principal) {
        boolean ok = this.commentService.deleteComment(commentId, principal.getName());
        if (!ok) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PostMapping("/secure/courses/{courseId}/lessons")
    public ResponseEntity<Lesson> createLesson(@PathVariable("courseId") int courseId,
            @ModelAttribute Lesson l) {
        Course c = new Course();
        c.setId(courseId);
        l.setCourseId(c);

        this.lessonService.addOrUpdateLesson(l);
        return new ResponseEntity<>(l, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @PutMapping("/secure/lessons/{lessonId}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable("lessonId") int lessonId,
            @ModelAttribute Lesson l) {
        l.setId(lessonId);
        this.lessonService.addOrUpdateLesson(l);
        return new ResponseEntity<>(l, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @DeleteMapping("/secure/lessons/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable("lessonId") int lessonId) {
        this.lessonService.deleteLesson(lessonId);
    }
}