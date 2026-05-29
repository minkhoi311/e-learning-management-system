/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;

import com.lmk.pojo.User;
import com.lmk.services.UserService;
import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class ApiUserController {
    @Autowired
    private UserService userService;
    
// GET /api/user/current-user — Lấy thông tin user đang đăng nhập
    @GetMapping("/user/current-user")
    public ResponseEntity<Object> currentUser(Principal principal) {
        if (principal == null)
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);

        User u = this.userService.getUserByUsername(principal.getName());

        if (u == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        u.setPassword(null);

        return new ResponseEntity<>(u, HttpStatus.OK);
    }

// PATCH /api/user/current-user — Cập nhật thông tin cá nhân
@PatchMapping("/user/current-user")
public ResponseEntity<Object> updateCurrentUser(@ModelAttribute User u, Principal principal) {
    if (principal == null)
        return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);

    User updated = this.userService.updateCurrentUser(u, principal.getName());

    if (updated == null)
        return new ResponseEntity<>(Map.of("message", "Cập nhật thất bại!"), HttpStatus.BAD_REQUEST);

    updated.setPassword(null);

    return new ResponseEntity<>(updated, HttpStatus.OK);
}

    // PATCH /api/user/current-user/password — Đổi mật khẩu
    @PatchMapping("/user/current-user/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> body, Principal principal) {

        if (principal == null)
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);

        boolean ok = this.userService.changePassword(
                principal.getName(),
                body.get("old_password"),
                body.get("new_password"));

        if (!ok)
            return new ResponseEntity<>(
                    Map.of("message", "Mật khẩu cũ không đúng hoặc mật khẩu mới quá ngắn (tối thiểu 6 ký tự)!"),
                    HttpStatus.BAD_REQUEST
            );

        return new ResponseEntity<>(
                Map.of("message", "Đổi mật khẩu thành công!"),
                HttpStatus.OK
        );
    }
}
