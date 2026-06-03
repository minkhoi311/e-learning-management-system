package com.lmk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lmk.pojo.Lesson;
import com.lmk.repositories.LessonRepository;
import com.lmk.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Lesson> getLessons(Map<String, String> params) {
        return this.lessonRepo.getLessons(params);
    }

    @Override
    public Lesson getLessonById(int id) {
        return this.lessonRepo.getLessonById(id);
    }
    
    @Override
    public Long countLessons(Map<String, String> params) {
        return this.lessonRepo.countLessons(params);
    }

    @Override
    public void addOrUpdateLesson(Lesson l) {
        if (l.getFile() != null && !l.getFile().isEmpty()) {
            try {
                Map res = this.cloudinary.uploader().upload(
                    l.getFile().getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                l.setImage(res.get("secure_url").toString());
            } catch (IOException ex) {
                Logger.getLogger(LessonServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (l.getId() == null) l.setCreatedTime(new Date());
        else l.setUpdatedTime(new Date());

        this.lessonRepo.addOrUpdateLesson(l);
    }


    @Override
    public void deleteLesson(int id) {
        this.lessonRepo.deleteLesson(id);
    }
}