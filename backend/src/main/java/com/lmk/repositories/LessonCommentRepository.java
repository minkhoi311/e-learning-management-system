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
    public List<LessonComment> getByLesson(int lessonId);
    
    public LessonComment getById(int id);
    
    public LessonComment add(LessonComment c);
    
    public boolean delete(int id);
}
