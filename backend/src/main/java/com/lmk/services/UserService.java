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
    User updateUser(User user);
    User updateCurrentUser(User u, String username);
    boolean changePassword(String username, String oldPassword, String newPassword);
    boolean unActiveUser(int id);
    boolean activeUser(int id);
    boolean approveInstructor(int id);
    User addUser(User user);
    User addUser(Map<String, String> params, MultipartFile avatar);
    UserDetails loadUserByUsername(String username);
    boolean authenticate(String username, String password);
}
