/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.User;
import com.lmk.services.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Acer
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiAuthController {
    @Autowired
    private UserService userService;
    
    // POST /api/register — Đăng ký tài khoản (PUBLIC)
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User u) {

        // Validate cơ bản
        if (u.getUsername() == null || u.getUsername().isBlank())
            return new ResponseEntity<>(Map.of("message", "Username không được để trống!"), HttpStatus.BAD_REQUEST);

        if (u.getPassword() == null || u.getPassword().length() < 6)
            return new ResponseEntity<>(Map.of("message", "Mật khẩu phải có ít nhất 6 ký tự!"),HttpStatus.BAD_REQUEST);

        if (u.getEmail() == null || u.getEmail().isBlank())
            return new ResponseEntity<>(Map.of("message", "Email không được để trống!"),HttpStatus.BAD_REQUEST);

        try {
            User saved = this.userService.addUser(u);
            saved.setPassword(null);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (Exception ex) {
            return new ResponseEntity<>(Map.of("message", "Username hoặc Email đã tồn tại!"),HttpStatus.CONFLICT);
        }
    }
}
