/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.Enrollment;
import com.lmk.repositories.EnrollmentRepository;
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
public class EnrollmentRepositoryImpl implements EnrollmentRepository{
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Enrollment> getByStudent(int studentId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Enrollment> q = b.createQuery(Enrollment.class);
        
        Root<Enrollment> root = q.from(Enrollment.class);
        q.select(root);
        q.where(b.equal(root.get("studentId"), studentId));
        
        Query<Enrollment> query = session.createQuery(q);
        return query.getResultList();
    }

    @Override
    public Enrollment getById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Enrollment.class, id);
    }

    @Override
    public Enrollment getByStudentAndCourse(int studentId, int courseId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Enrollment> q = b.createQuery(Enrollment.class);
        
        Root<Enrollment> root = q.from(Enrollment.class);
        q.select(root);
        q.where(b.and(b.equal(root.get("studentId"), studentId), b.equal(root.get("courseId"), courseId)));
        Query<Enrollment> query = session.createQuery(q);
         return query.getSingleResult();
    }

    @Override
    public Enrollment enroll(Enrollment e) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(e);
        return e;
    }

    @Override
    public Enrollment update(Enrollment e) {
        Session session = this.factory.getObject().getCurrentSession();
        return (Enrollment) session.merge(e);
    }
    
    

}
