/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.User;
import com.lmk.services.UserService;
import com.lmk.utils.JwtUtils;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/users")
    public ResponseEntity<User> create(@RequestParam Map<String, String> info,
            @RequestParam(value = "avatar") MultipartFile avatar) {
        User u = this.userService.addUser(info, avatar);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {
        if (this.userService.authenticate(u.getUsername(), u.getPassword())) {
            try {
                String token = JwtUtils.generateToken(u.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
    }

    @RequestMapping("/secure/profile")
    @ResponseBody //Trả dữ liệu trực tiếp vào HTTP response
    public ResponseEntity<User> getProfile(Principal principal) {
        return new ResponseEntity<>(this.userService.getUserByUsername(principal.getName()), HttpStatus.OK);
    }

    @PatchMapping("/secure/profile")
    public ResponseEntity<Object> updateCurrentUser(@ModelAttribute User u, Principal principal) {
        return new ResponseEntity<>(this.userService.updateCurrentUser(u, principal.getName()), HttpStatus.OK);
    }

    @PostMapping("/secure/change-password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody Map<String, String> body, Principal principal) {
        String oldPassword = body.get("old_password");
        String newPassword = body.get("new_password");
        this.userService.changePassword(principal.getName(), oldPassword, newPassword);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/secure/users/approve")
    public ResponseEntity<Object> approveInstructor(@RequestBody Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("id")) {
                return new ResponseEntity<>(
                        Map.of("message", "Thiếu ID người dùng!"),
                        HttpStatus.BAD_REQUEST
                );
            }
            int id = Integer.parseInt(payload.get("id").toString());
            boolean ok = this.userService.approveInstructor(id);

            if (!ok) {
                return new ResponseEntity<>(
                        Map.of("message", "Không tìm thấy hoặc không phải giảng viên!"),
                        HttpStatus.NOT_FOUND
                );
            }

            return new ResponseEntity<>(
                    Map.of("message", "Đã duyệt thành công!"),
                    HttpStatus.OK
            );

        } catch (NumberFormatException e) {
            return new ResponseEntity<>(
                    Map.of("message", "ID không hợp lệ!"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

}
