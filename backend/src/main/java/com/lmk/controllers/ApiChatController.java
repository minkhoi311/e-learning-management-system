package com.lmk.controllers;

import com.lmk.pojo.ChatSession;
import com.lmk.services.ChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiChatController {
    @Autowired
    private ChatSessionService chatSessionService;

    @PostMapping("/secure/chat/room")
    public ResponseEntity<ChatSession> getOrCreateRoom(@RequestBody Map<String, Integer> payload, Principal principal) {
        if (principal == null) 
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Integer targetId = payload.get("target_id");
        if (targetId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            ChatSession session = chatSessionService.getOrCreateRoom(principal.getName(), targetId);
            return new ResponseEntity<>(session, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/secure/chat/sessions")
    public ResponseEntity<List<ChatSession>> getSessionList(Principal principal) {
        if (principal == null) 
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<ChatSession> sessions = chatSessionService.getChatSessions(principal.getName());
        return new ResponseEntity<>(sessions, HttpStatus.OK);
    }
}