/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.services;

import com.lmk.pojo.LessonComment;
import java.util.List;

/**
 *
 * @author Acer
 */
public interface LessonCommentService {
    public List<LessonComment> getByLesson(int lessonId);
    public LessonComment add(int lessonId, String content, Integer parentId, String username);
    public boolean delete(int commentId, String username);
}
