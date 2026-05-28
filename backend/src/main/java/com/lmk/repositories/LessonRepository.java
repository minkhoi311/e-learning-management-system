package com.lmk.repositories;

import com.lmk.pojo.Lesson;
import java.util.List;
import java.util.Map;

public interface LessonRepository {
    public List<Lesson> getLessons(Map<String, String> params);
    
    public Lesson getLessonById(int id);
    
    public void addOrUpdateLesson(Lesson lesson);
    
    public void deleteLesson(int id);
    
    public Long countLesson(Map<String, String> params);
}