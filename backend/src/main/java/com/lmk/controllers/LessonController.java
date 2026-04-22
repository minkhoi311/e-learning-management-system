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

    // 1. DANH SÁCH BÀI HỌC (Có Lọc & Phân trang)
    @GetMapping("/lessons")
    public String listLessons(Model model, @RequestParam Map<String, String> params) {
        // Đảm bảo params luôn có key "page" để Repo biết đường phân trang
        if (!params.containsKey("page")) {
            params.put("page", "1");
        }

        List<Lesson> lessons = lessonService.getLessons(params);
        model.addAttribute("lessons", lessons);

        // Gửi danh sách khóa học để làm Dropdown Lọc
        model.addAttribute("courses", courseService.getCourses(null));

        // Phân trang
        Long totalItems = lessonService.countLesson(params);
        int pageSize = Integer.parseInt(env.getProperty("lessons.page_size", "6"));
        int totalPages = DaoUtils.calculateTotalPages(totalItems, pageSize);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));
        // Trả về file lesson.html (Trang danh sách)
        return "lesson"; 
    }

    // 2. MỞ FORM THÊM MỚI
    @GetMapping("/lessons/add")
    public String addView(Model model) {
        model.addAttribute("lesson", new Lesson());
        // Lấy danh sách khóa học gán vào dropdown khi thêm bài học
        model.addAttribute("courses", courseService.getCourses(null));
        // Trả về file lesson-detail.html (Bạn đã làm ở bước trước)
        return "lesson-detail";
    }

    // 3. XỬ LÝ LƯU DỮ LIỆU (Thêm/Sửa)
    @PostMapping("/lessons/add")
    public String create(@ModelAttribute(value = "lesson") Lesson l) {
        this.lessonService.addOrUpdateLesson(l);
        return "redirect:/admin/lessons";
    }

    // 4. MỞ FORM CẬP NHẬT
    @GetMapping("/lessons/{lessonId}")
    public String updateView(Model model, @PathVariable(value = "lessonId") int id) {
        model.addAttribute("lesson", this.lessonService.getLessonById(id));
        model.addAttribute("courses", courseService.getCourses(null));
        return "lesson-detail";
    }
    
    @GetMapping("/lessons/delete/{lessonId}")
    public String deleteLesson(@PathVariable(value = "lessonId") int id) {
        // Gọi Service để xóa bài học theo ID
        this.lessonService.deleteLesson(id);
        
        // Xóa xong thì load lại trang danh sách bài học
        return "redirect:/admin/lessons";
    }
}