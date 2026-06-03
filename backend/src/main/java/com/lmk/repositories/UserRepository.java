/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Acer
 */
public interface UserRepository {
    List<User> getUsers(Map<String, String> params);
    Long countUsers(Map<String, String> params);
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> getUsersByRole(String role);
    void saveUser(User u);
    boolean authenticate(String username, String password);
}
