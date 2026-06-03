///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.lmk.services.impl;
//
//import com.lmk.pojo.ChatSession;
//import com.lmk.pojo.User;
//import com.lmk.repositories.ChatSessionRepository;
//import com.lmk.repositories.UserRepository;
//import com.lmk.services.ChatSessionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
///**
// *
// * @author Acer
// */
//@Service
//public class ChatSessionServiceImpl implements ChatSessionService {
//@Autowired
//    private ChatSessionRepository chatRepo;
//
//    @Autowired
//    private UserRepository userRepo;
//
//    @Override
//    public void getOrCreateRoom(String currentUsername, int targetId) {
//        User currentUser = userRepo.getUserByUsername(currentUsername);
//        User targetUser = userRepo.getUserById(targetId);
//
//        if (currentUser == null || targetUser == null) {
//            throw new IllegalArgumentException("Không tìm thấy người dùng này.");
//        }
//
//        int smallId = Math.min(currentUser.getId(), targetUser.getId());
//        int largeId = Math.max(currentUser.getId(), targetUser.getId());
//        String roomId = "room_" + smallId + "_" + largeId;
//
//        ChatSession existing = chatRepo.getByRoomId(roomId);
//        if (existing != null) {
//            return existing;
//        }
//
//        ChatSession newSession = new ChatSession();
//        newSession.setFirebaseRoom(roomId);
//
//        // Gọi đúng phương thức setter từ POJO của bạn
//        if ("STUDENT".equals(currentUser.getRole())) {
//            newSession.setStudentId(currentUser);
//            newSession.setInstructorId(targetUser);
//        } else {
//            newSession.setInstructorId(currentUser);
//            newSession.setStudentId(targetUser);
//        }
//
//        chatRepo.saveOrUpdate(newSession);
//    }
//
//    @Override
//    public List<ChatSession> getChatSessions(String username) {
//        User user = userRepo.getUserByUsername(username);
//        return chatRepo.getSessionsByUser(user);
//    }
//}
