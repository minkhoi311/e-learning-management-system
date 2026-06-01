package com.lmk.repositories;

import com.lmk.pojo.Lesson;
import java.util.List;
import java.util.Map;

public interface LessonRepository {

    List<Lesson> getLessons(Map<String, String> params);

    Lesson getLessonById(int id);

    void addOrUpdateLesson(Lesson lesson);

    void deleteLesson(int id);

    Long countLesson(Map<String, String> params);

    Long countLessonByCourseId(int courseId);
}
