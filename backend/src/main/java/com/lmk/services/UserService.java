/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.services;

import com.lmk.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
public interface UserService  extends UserDetailsService{
    List<User> getUsers(Map<String, String> params);
    Long countUsers(Map<String, String> params);
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> getUsersByRole(String role);
    User addUser(Map<String, String> params, MultipartFile avatar);
    void saveUser(User u);
    User updateProfile(User input, String username);
    void changePassword(String username, String oldPassword, String newPassword);
    boolean approveInstructor(int id);
    boolean authenticate(String username, String password);
}
