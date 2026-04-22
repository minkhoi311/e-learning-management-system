/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.User;
import java.util.List;

/**
 *
 * @author Acer
 */
public interface UserReposity {
    public User getUserByUsername(String username);
    public User addUser(User u);
    public List<User> getUsersByRole(String role);
}
