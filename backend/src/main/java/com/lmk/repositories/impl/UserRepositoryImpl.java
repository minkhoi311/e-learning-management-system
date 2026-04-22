/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.User;
import com.lmk.repositories.UserReposity;
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
public class UserRepositoryImpl implements UserReposity{

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public User getUserByUsername(String username) {
        Session session = this.factory.getObject().getCurrentSession();
        Query q = session.createNamedQuery("User.findByUsername", User.class);
        q.setParameter("username", username);

        return (User) q.getSingleResult();

    }

    @Override
    public User addUser(User u) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(u);
        return u;
    }

    @Override
    public List<User> getUsersByRole(String role) {
        Session session = this.factory.getObject().getCurrentSession();

        Query<User> q = session.createQuery("FROM User WHERE role = :role", User.class);
        q.setParameter("role", role);
        return q.getResultList();
    }
}
