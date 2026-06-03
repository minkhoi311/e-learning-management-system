/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.services;
import com.lmk.pojo.ChatSession;
import java.util.List;
/**
 *
 * @author Acer
 */
public interface ChatSessionService {
    ChatSession getOrCreateRoom(String currentUsername, int targetId);
    List<ChatSession> getChatSessions(String username);
}
