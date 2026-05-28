/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.Course;
import com.lmk.repositories.CourseRepository;
import com.lmk.services.CategoryService;
import com.lmk.services.CourseService;
import com.lmk.services.UserService;
import com.lmk.utils.DaoUtils;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Acer
 */

@Controller
@RequestMapping("/admin")
public class CourseController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private Environment env;
    
    
    @GetMapping("/courses")
    public String listCourses(Model model, @RequestParam Map<String, String> params) {
        
        // 1. Lấy danh sách khóa học (Đã áp dụng Filter và Phân trang bên Repository)
        List<Course> list = courseService.getCourses(params);
        model.addAttribute("courses", list);

        // 2. Gửi thêm Categories để đổ vào giao diện (nếu bạn có thanh tìm kiếm theo danh mục)
        model.addAttribute("categories", categoryService.getCates());

        // 3. LOGIC PHÂN TRANG
        // Đếm tổng số khóa học thỏa mãn điều kiện lọc
        Long totalItems = courseService.countCourse(params);
        
        // Lấy số mục trên mỗi trang từ cấu hình (Mặc định là 6 nếu không tìm thấy)
        int pageSize = Integer.parseInt(env.getProperty("courses.page_size", "6"));

        // Tính tổng số trang bằng Utils
        int totalPages = DaoUtils.calculateTotalPages(totalItems, pageSize);

        // Đẩy xuống Model để HTML hiển thị nút bấm
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", Integer.parseInt(params.getOrDefault("page", "1")));

        return "course"; 
    }
    
    // 2. MỞ form Thêm mới (Phương thức GET)
    @GetMapping("/courses/add")
    public String addView(Model model) {
        // Gửi một đối tượng Course rỗng để Thymeleaf map form (th:object)
        model.addAttribute("course", new Course());
        // Cần categories cho cái Dropdown chọn danh mục
        model.addAttribute("categories", categoryService.getCates());
        model.addAttribute("instructors", userService.getUsersByRole("INSTRUCTOR"));
        return "course-detail"; 
    }
    
    // 3. XỬ LÝ lưu dữ liệu khi submit form (Phương thức POST)
    @PostMapping("/courses/add")
    public String create(@ModelAttribute(value = "course") Course c) {
        this.courseService.addOrUpdateCourse(c);
        // Sửa lại: redirect về danh sách khóa học của admin thay vì trang chủ
        return "redirect:/admin/courses";
    }
    
    // 4. MỞ form Cập nhật (Phương thức GET)
    @GetMapping("/courses/{courseId}")
    public String updateView(Model model, @PathVariable(value = "courseId") int id) {
        // Sửa "courses" thành "course" cho khớp với file html
        model.addAttribute("course", this.courseService.getCourseById(id));
        // Bổ sung danh sách categories cho Dropdown
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
    
    // 6. XÓA KHÓA HỌC
    @GetMapping("/courses/delete/{courseId}")
    public String delete(@PathVariable int courseId,
                         RedirectAttributes redirectAttrs) {
        boolean ok = this.courseService.deleteCourse(courseId);
        if (ok) {
            redirectAttrs.addFlashAttribute("successMsg", "Xóa khóa học thành công!");
        } else {
            redirectAttrs.addFlashAttribute("errMsg", "Không tìm thấy khóa học!");
        }
        return "redirect:/admin/courses";
    }
    
    
}
