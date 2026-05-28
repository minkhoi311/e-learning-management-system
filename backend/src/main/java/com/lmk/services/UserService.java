/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.services;

import com.lmk.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Acer
 */
public interface UserService {

    public List<User> getUsers(Map<String, String> params);

    public Long countUsers(Map<String, String> params);

    public User getUserById(int id);
    
    public User getUserByUsername(String username);

    public List<User> getUsersByRole(String role);

    public User addUser(User user);

    public User updateUser(User user);
    
    public User updateCurrentUser(User u, String username);
    
    public boolean changePassword(String username, String oldPassword, String newPassword);
    
    public boolean unActiveUser(int id);
    
    public boolean activeUser(int id);

    public boolean approveInstructor(int id);
}
