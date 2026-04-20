/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.User;
import java.util.List;

/**
 *
 * @author admin
 */
public interface UserRepository {
    User getUserByUsername(String username);
    User addUser(User u);
    boolean authenticate(String username, String password);
    
    
    public List<User> getInstructors();
}
