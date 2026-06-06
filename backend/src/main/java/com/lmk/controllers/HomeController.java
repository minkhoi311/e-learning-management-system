/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.services.CategoryService;
import com.lmk.services.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Acer
 */
@Controller
@ControllerAdvice
public class HomeController {
    @Autowired
    private UserService userService;
    
    
    @RequestMapping("/")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("currentUser",
                    this.userService.getUserByUsername(principal.getName()));
        }
        return "index";
    }
}
