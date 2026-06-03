/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lmk.repositories;

import com.lmk.pojo.LessonProgress;

/**
 *
 * @author Acer
 */
public interface LessonProgressRepository {
    Long countCompletedLessons(int enrollmentId);
    void saveProgress(LessonProgress progress);
}
