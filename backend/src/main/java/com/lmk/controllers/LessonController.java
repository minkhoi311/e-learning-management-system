package com.lmk.controllers;

import com.lmk.pojo.Lesson;
import com.lmk.services.CourseService;
import com.lmk.services.LessonService;
import com.lmk.utils.DaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private Environment env;

    @GetMapping("/lessons")
    public String listLessons(Model model, @RequestParam Map<String, String> params) {
        int currentPage = Integer.parseInt(params.getOrDefault("page", "1"));

        List<Lesson> lessons = lessonService.getLessons(params);
        model.addAttribute("lessons", lessons);
        model.addAttribute("courses", courseService.getCourses(null));

        Long totalItems = lessonService.countLessons(params);
        int pageSize = Integer.parseInt(env.getProperty("lessons.page_size", "6"));
        int totalPages = DaoUtils.calculateTotalPages(totalItems, pageSize);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
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