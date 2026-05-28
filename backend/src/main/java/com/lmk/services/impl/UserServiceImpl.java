/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lmk.pojo.User;
import com.lmk.repositories.UserRepository;
import com.lmk.services.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Acer
 */
@Service
public class UserServiceImpl implements UserService{
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private Cloudinary cloudinary;
    
    
    private void processAvatarUpload(User user) {
        if (user.getFile() != null && !user.getFile().isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(user.getFile().getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                user.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        return this.userRepo.getUsers(params);
    }

    @Override
    public Long countUsers(Map<String, String> params) {
        return this.userRepo.countUsers(params);
    }

    @Override
    public User getUserById(int id) {
        return this.userRepo.getUserById(id);
    }

    @Override
    public boolean unActiveUser(int id) {
       return this.userRepo.unActiveUser(id);
    }
    
    @Override
    public boolean activeUser(int id){
        return this.userRepo.activeUser(id);
    }

    @Override
    public boolean approveInstructor(int id) {
        return this.userRepo.approveInstructor(id);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return this.userRepo.getUsersByRole(role);
    }

    @Override
    public User addUser(User user) {
        processAvatarUpload(user);
        return this.userRepo.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = this.userRepo.getUserById(user.getId());
        // Cập nhật các trường được phép thay đổi từ form
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        // Cập nhật các cờ trạng thái
        existingUser.setIsActive(user.getIsActive());
        existingUser.setIsInstructor(user.getIsInstructor());
        existingUser.setIsAdmin(user.getIsAdmin());
       
        processAvatarUpload(user); 
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            existingUser.setAvatar(user.getAvatar());
        }

        return this.userRepo.updateUser(existingUser);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public User updateCurrentUser(User u, String username) {
        User existing = this.userRepo.getUserByUsername(username);
        if (existing == null) return null;

        if (u.getFirstName() != null && !u.getFirstName().isBlank())
            existing.setFirstName(u.getFirstName());
        if (u.getLastName() != null && !u.getLastName().isBlank())
            existing.setLastName(u.getLastName());

        processAvatarUpload(u);
        if (u.getAvatar() != null && !u.getAvatar().isEmpty())
            existing.setAvatar(u.getAvatar());

        return this.userRepo.updateUser(existing);
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) return false;
        User u = this.userRepo.getUserByUsername(username);
        if (u == null) return false;
        if (!u.getPassword().equals(oldPassword)) return false;
        u.setPassword(newPassword);
        this.userRepo.updateUser(u);
        return true;
    }
    
}
