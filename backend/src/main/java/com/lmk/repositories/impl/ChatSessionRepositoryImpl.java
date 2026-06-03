/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.ChatSession;
import com.lmk.pojo.User;
import com.lmk.repositories.ChatSessionRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 *
 * @author Acer
 */
@Repository
@Transactional
public class ChatSessionRepositoryImpl implements ChatSessionRepository {
@Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public ChatSession getByRoomId(String roomId) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<ChatSession> q = b.createQuery(ChatSession.class);
        Root<ChatSession> root = q.from(ChatSession.class);
        q.select(root).where(b.equal(root.get("firebaseRoom"), roomId));

        Query<ChatSession> query = session.createQuery(q);
        return query.uniqueResult();
    }

    @Override
    public void saveOrUpdate(ChatSession chatSession) {
        Session session = this.factory.getObject().getCurrentSession();   
        if (chatSession.getId() != null) {
            session.merge(chatSession);
        } else {
            session.persist(chatSession);
        }
    }

    @Override
    public List<ChatSession> getSessionsByUser(User user) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<ChatSession> q = b.createQuery(ChatSession.class);
        Root<ChatSession> root = q.from(ChatSession.class);

        if ("INSTRUCTOR".equals(user.getRole())) {
            q.select(root).where(b.equal(root.get("instructorId").get("id"), user.getId()));
        } else {
            q.select(root).where(b.equal(root.get("studentId").get("id"), user.getId()));
        }
        Query<ChatSession> query = session.createQuery(q);
        return query.getResultList();
    }
}
