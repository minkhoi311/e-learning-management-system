/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.LessonComment;
import java.util.List;

/**
 *
 * @author Acer
 */

public interface LessonCommentRepository {
    List<LessonComment> getByLesson(int lessonId);
    
    LessonComment getById(int id);
    
    LessonComment add(LessonComment c);
    
    boolean delete(int id);
}
