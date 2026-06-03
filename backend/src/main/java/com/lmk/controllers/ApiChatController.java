/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.controllers;
import com.lmk.pojo.ChatSession;
import com.lmk.services.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Acer
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiChatController {
    @Autowired
    private ChatSessionService chatSessionService;

    @PostMapping("/secure/chat/room")
    public ResponseEntity<Object> getOrCreateRoom(@RequestBody Map<String, Integer> payload, Principal principal) {
        if (principal == null) 
            return new ResponseEntity<>(Map.of("message", "Chưa đăng nhập!"), HttpStatus.UNAUTHORIZED);

        Integer targetId = payload.get("target_id");
        if (targetId == null) {
            return new ResponseEntity<>(Map.of("message", "Thiếu tham số target_id"), HttpStatus.BAD_REQUEST);
        }

        try {
            ChatSession session = chatSessionService.getOrCreateRoom(principal.getName(), targetId);
            return new ResponseEntity<>(Map.of("firebase_room", session.getFirebaseRoom()), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/secure/chat/sessions")
    public ResponseEntity<Object> getSessionList(Principal principal) {
        if (principal == null) 
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<ChatSession> sessions = chatSessionService.getChatSessions(principal.getName());

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (ChatSession s : sessions) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("firebase_room", s.getFirebaseRoom());
            
            // Gọi đúng hàm Getter từ POJO của bạn
            map.put("student_id", s.getStudentId() != null ? s.getStudentId().getId() : null);
            map.put("student_username", s.getStudentId() != null ? s.getStudentId().getUsername() : null);
            
            map.put("instructor_id", s.getInstructorId() != null ? s.getInstructorId().getId() : null);
            map.put("instructor_username", s.getInstructorId() != null ? s.getInstructorId().getUsername() : null);
            
            responseList.add(map);
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
}
