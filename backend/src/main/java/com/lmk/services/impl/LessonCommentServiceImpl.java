/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services.impl;

import com.lmk.pojo.Lesson;
import com.lmk.pojo.LessonComment;
import com.lmk.pojo.User;
import com.lmk.repositories.LessonCommentRepository;
import com.lmk.repositories.LessonRepository;
import com.lmk.repositories.UserRepository;
import com.lmk.services.LessonCommentService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Acer
 */
@Service
public class LessonCommentServiceImpl implements LessonCommentService{
    @Autowired
    private LessonCommentRepository commentRepo;

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private UserRepository userRepo;
    
    @Override
    public List<LessonComment> getByLesson(int lessonId) {
        return this.commentRepo.getByLesson(lessonId);    }

    @Override
    public LessonComment add(int lessonId, String content, Integer parentId, String username) {
        if (content == null || content.isBlank()) return null;
        User u = this.userRepo.getUserByUsername(username);
        Lesson lesson = this.lessonRepo.getLessonById(lessonId);
        if (u == null || lesson == null) return null;
         
        LessonComment c = new LessonComment();
        c.setContent(content.trim());
        c.setLessonId(lesson);
        c.setUserId(u);
        c.setCreatedTime(new Date());
        return this.commentRepo.add(c);
    }

    @Override
    public boolean delete(int commentId, String username) {
        LessonComment c = this.commentRepo.getById(commentId);
        if (c == null) return false;
        boolean isOwner = c.getUserId().getUsername().equals(username);
        if(!isOwner) return false;
        return this.commentRepo.delete(commentId);
    }
    
}
