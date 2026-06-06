package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.services.CategoryService;
import com.lmk.services.CourseService;
import com.lmk.services.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class CourseController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    

    @GetMapping("/courses")
    public String listCourses(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("courses", courseService.getCourses(params));
        model.addAttribute("categories", categoryService.getCates());
        return "course"; 
    }
    
    @GetMapping("/courses/add")
    public String addView(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", categoryService.getCates());
        model.addAttribute("instructors", userService.getUsersByRole("INSTRUCTOR"));
        return "course-detail"; 
    }
    
    @PostMapping("/courses/add")
    public String create(@ModelAttribute(value = "course") Course c) {
        this.courseService.addOrUpdateCourse(c);
        return "redirect:/admin/courses";
    }
    
    @GetMapping("/courses/{courseId}")
    public String updateView(Model model, @PathVariable(value = "courseId") int id) {
        model.addAttribute("course", this.courseService.getCourseById(id));
        model.addAttribute("categories", categoryService.getCates());
        model.addAttribute("instructors", userService.getUsersByRole("INSTRUCTOR"));
        return "course-detail";
    }
    
    @PostMapping("/courses/{courseId}")
    public String update(@PathVariable int courseId,
                         @ModelAttribute(value = "course") Course c) {
        c.setId(courseId);
        this.courseService.addOrUpdateCourse(c);
        return "redirect:/admin/courses";
    }
}