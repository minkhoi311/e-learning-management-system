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
    List<LessonComment> getByLesson(int lessonId);
    LessonComment addComment(int lessonId, String content, String username);
    boolean deleteComment(int commentId, String username);
}
