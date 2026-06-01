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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    //tao user
    @PostMapping(path = "/users",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@RequestParam Map<String, String> params,
            @RequestParam(value = "avatar") MultipartFile avatar) {
        User u = this.userService.addUser(params, avatar);

        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    //login
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

    //Lay thong tin user
    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<User> getProfile(Principal principal) {
        if (principal == null) 
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        User u = this.userService.getUserByUsername(principal.getName());
        if (u == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        u.setPassword(null);
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping("/secure/profile")
    public ResponseEntity<Object> updateCurrentUser(@ModelAttribute User u, Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        User updated = this.userService.updateCurrentUser(u, principal.getName());

        if (updated == null) {
            return new ResponseEntity<>(Map.of("message", "Cập nhật thất bại!"), HttpStatus.BAD_REQUEST);
        }

        updated.setPassword(null);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PostMapping("/secure/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> body, Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);
        }

        boolean ok = this.userService.changePassword(
                principal.getName(),
                body.get("old_password"),
                body.get("new_password"));
        if (!ok) {
            return new ResponseEntity<>(
                    Map.of("message", "Mật khẩu cũ không đúng hoặc mật khẩu mới quá ngắn (tối thiểu 6 ký tự)!"),
                    HttpStatus.BAD_REQUEST
            );
        }
        return new ResponseEntity<>(
                Map.of("message", "Đổi mật khẩu thành công!"),
                HttpStatus.OK
        );
    }

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
