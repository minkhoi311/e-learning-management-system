package com.lmk.controllers;

import com.lmk.pojo.Lesson;
import com.lmk.services.CourseService;
import com.lmk.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private CourseService courseService;

    @GetMapping("/lessons")
    public String listLessons(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("lessons", lessonService.getLessons(params));
        model.addAttribute("courses", courseService.getCourses(null));
        return "lesson";
    }

    @GetMapping("/lessons/add")
    public String addView(Model model) {
        model.addAttribute("lesson", new Lesson());
        model.addAttribute("courses", courseService.getCourses(null));
        return "lesson-detail";
    }

    @PostMapping("/lessons/add")
    public String create(@ModelAttribute(value = "lesson") Lesson l) {
        this.lessonService.addOrUpdateLesson(l);
        return "redirect:/admin/lessons";
    }

    @GetMapping("/lessons/{lessonId}")
    public String updateView(Model model, @PathVariable(value = "lessonId") int id) {
        Lesson currentLesson = this.lessonService.getLessonById(id);
        model.addAttribute("lesson", currentLesson);
        model.addAttribute("courses", courseService.getCourses(null));

        if (currentLesson.getCourseId() != null) {
            model.addAttribute("selectedCourseId", currentLesson.getCourseId().getId());
        }

        return "lesson-detail";
    }
}