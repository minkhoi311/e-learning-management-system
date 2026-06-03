/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.ChatSession;
import com.lmk.pojo.User;
import java.util.List;

/**
 *
 * @author Acer
 */
public interface ChatSessionRepository {
    ChatSession getByRoomId(String roomId);
    void saveOrUpdate(ChatSession chatSession);
    List<ChatSession> getSessionsByUser(User user);
}
