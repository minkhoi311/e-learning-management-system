/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.LessonProgress;
import com.lmk.repositories.LessonProgressRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
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
public class LessonProgressRepositoryImpl implements LessonProgressRepository {
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public Long countCompletedLessons(int enrollmentId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<LessonProgress> root = q.from(LessonProgress.class);
        q.select(b.count(root));
        Predicate p1 = b.equal(root.get("enrollmentId").get("id"), enrollmentId);

        Predicate p2 = b.equal(root.get("isCompleted"), Boolean.TRUE);

        q.where(b.and(p1, p2));

        return session.createQuery(q).getSingleResult();
    }

    @Override
    public void saveProgress(LessonProgress progress) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(progress);
    }

}
