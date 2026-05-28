/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.Enrollment;
import com.lmk.pojo.LessonComment;
import com.lmk.repositories.LessonCommentRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */

@Repository
@Transactional
public class LessonCommentRepositoryImpl implements LessonCommentRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<LessonComment> getByLesson(int lessonId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<LessonComment> q = b.createQuery(LessonComment.class);
        Root<LessonComment> root = q.from(LessonComment.class);
        q.select(root);
        q.where(b.equal(root.get("lessonId"), lessonId));
        Query<LessonComment> query = session.createQuery(q);
        return query.getResultList();
    }
    
    @Override
    public LessonComment getById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(LessonComment.class, id);
    }
    
    @Override
    public LessonComment add(LessonComment c) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(c);
        return c;
    }

    @Override
    public boolean delete(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        LessonComment c = session.get(LessonComment.class, id);
        if (c == null) return false;
        session.remove(c);
        return true;
    }
}
