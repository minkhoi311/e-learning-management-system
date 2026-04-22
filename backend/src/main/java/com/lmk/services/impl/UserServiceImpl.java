/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.lmk.pojo.User;
import com.lmk.repositories.UserReposity;
import com.lmk.services.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Acer
 */
@Service
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserReposity userRepo;
    
    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public User addUser(User u) {
        return this.userRepo.addUser(u);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return this.userRepo.getUsersByRole(role);
    }
}
