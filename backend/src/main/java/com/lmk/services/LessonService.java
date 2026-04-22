/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.services;

import com.lmk.pojo.Lesson;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Acer
 */
public interface LessonService {
    public List<Lesson> getLessons(Map<String, String> params);
    public Lesson getLessonById(int id);
    public void addOrUpdateLesson(Lesson l);
    public void deleteLesson(int id);
    public Long countLesson(Map<String, String> params);        
}
