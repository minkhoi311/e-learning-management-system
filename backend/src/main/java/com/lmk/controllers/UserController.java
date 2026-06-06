package com.lmk.controllers;

import com.lmk.pojo.User;
import com.lmk.services.UserService;
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

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    
    @GetMapping("/users")
    public String listUsers(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("users", userService.getUsers(params));
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
    public String saveUser(@ModelAttribute("user") User user) {
        if (user.getId() != null) {
            this.userService.saveUser(user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }
}