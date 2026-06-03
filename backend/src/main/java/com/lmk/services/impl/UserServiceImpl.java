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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Acer
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public List<User> getUsersByRole(String role) {
        return this.userRepo.getUsersByRole(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return this.userRepo.getUserByUsername(username);
    }

    @Override
    public User addUser(Map<String, String> params, MultipartFile avatar) {
        User u = new User();
        u.setFirstName(params.get("firstName"));
        u.setLastName(params.get("lastName"));
        u.setPhone(params.get("phone"));
        u.setEmail(params.get("email"));
        u.setUsername(params.get("username"));
        u.setPassword(passwordEncoder.encode(params.get("password")));
        u.setRole(params.getOrDefault("role", "STUDENT"));
        u.setIsInstructor(false);
        u.setIsAdmin(false);
        u.setAuthProvider("LOCAL");
        u.setIsActive(true); // Default là active
        u.setCreatedTime(new Date());

        if (avatar != null && !avatar.isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(avatar.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                u.setAvatar(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return this.userRepo.addUser(u);
    }
    
    @Override
    public User addUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) user.setRole("STUDENT");
        if (user.getIsInstructor() == null) user.setIsInstructor(false);
        if (user.getIsAdmin() == null) user.setIsAdmin(false);
        if (user.getIsActive() == null) user.setIsActive(true);
        user.setAuthProvider("LOCAL");
        user.setCreatedTime(new Date());
        
        processAvatarUpload(user);

        return this.userRepo.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = this.userRepo.getUserById(user.getId());

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setIsActive(user.getIsActive());
        existingUser.setIsInstructor(user.getIsInstructor());
        existingUser.setIsAdmin(user.getIsAdmin());
        existingUser.setUpdatedTime(new Date());
        processAvatarUpload(user);
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            existingUser.setAvatar(user.getAvatar());
        }
        return this.userRepo.updateUser(existingUser);
    }

    @Override
    public boolean unActiveUser(int id) {
        User user = this.userRepo.getUserById(id);
        if (user != null) {
            user.setIsActive(false);
            user.setUpdatedTime(new Date());
            this.userRepo.updateUser(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean activeUser(int id) {
        User user = this.userRepo.getUserById(id);
        if (user != null) {
            user.setIsActive(true);
            user.setUpdatedTime(new Date());
            this.userRepo.updateUser(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean approveInstructor(int id) {
        User user = this.userRepo.getUserById(id);
        if (user != null && "INSTRUCTOR".equals(user.getRole())) {
            user.setIsInstructor(true);
            user.setUpdatedTime(new Date());
            this.userRepo.updateUser(user);
            return true;
        }
        return false;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 3) {
            throw new IllegalArgumentException("Mật khẩu mới phải có tối thiểu 3 ký tự!");
        }
        
        User u = this.userRepo.getUserByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Người dùng không tồn tại trên hệ thống!");
        }
        if (!this.passwordEncoder.matches(oldPassword, u.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ nhập vào không chính xác!");
        }

        u.setPassword(this.passwordEncoder.encode(newPassword));
        u.setUpdatedTime(new Date());
        this.userRepo.updateUser(u);
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.userRepo.getUserByUsername(username);
        if (u == null) return false;
        return this.passwordEncoder.matches(password, u.getPassword());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tồn tại!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), authorities);
    }
    
    @Override
    public User updateCurrentUser(User u, String username) {
        User existing = this.userRepo.getUserByUsername(username);
        if (existing == null) {
            return null;
        }

        if (u.getFirstName() != null && !u.getFirstName().isBlank()) {
            existing.setFirstName(u.getFirstName());
        }
        if (u.getLastName() != null && !u.getLastName().isBlank()) {
            existing.setLastName(u.getLastName());
        }

        processAvatarUpload(u);
        if (u.getAvatar() != null && !u.getAvatar().isEmpty()) {
            existing.setAvatar(u.getAvatar());
        }
        existing.setUpdatedTime(new Date());

        return this.userRepo.updateUser(existing);
    }

}
