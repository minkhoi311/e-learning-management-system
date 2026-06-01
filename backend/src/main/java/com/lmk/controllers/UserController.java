/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.User;
import com.lmk.services.UserService;
import com.lmk.utils.DaoUtils;
import java.util.HashMap;
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
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    @GetMapping("/users")
    public String listUsers(Model model, @RequestParam Map<String, String> params) {
        if (!params.containsKey("page")) {
            params.put("page", "1");
        }

        // Lấy danh sách người dùng
        List<User> users = userService.getUsers(params);
        model.addAttribute("users", users);

        // Tính toán phân trang
        Long totalItems = userService.countUsers(params);
        int pageSize = Integer.parseInt(env.getProperty("users.page_size", "10"));
        int totalPages = DaoUtils.calculateTotalPages(totalItems, pageSize);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", Integer.parseInt(params.get("page")));

        return "user";
    }

    @GetMapping("/users/add")
    public String addView(Model model) {
        model.addAttribute("user", new User());
        return "user-detail";
    }

    @GetMapping("/users/{userId}")
    public String updateView(Model model, @PathVariable(value = "userId") int id) {
        model.addAttribute("user", this.userService.getUserById(id));
        return "user-detail";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttrs) {
        if (user.getId() != null) {
            this.userService.updateUser(user);
            redirectAttrs.addFlashAttribute("successMsg", "Cập nhật tài khoản thành công!");
        } else {
            this.userService.addUser(user);
            redirectAttrs.addFlashAttribute("successMsg", "Thêm người dùng thành công!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/login")
        public String loginView() {
            return "login";
        }
}
