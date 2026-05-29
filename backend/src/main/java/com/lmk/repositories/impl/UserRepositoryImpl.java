/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.User;
import com.lmk.repositories.UserRepository;
import com.lmk.utils.DaoUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Acer
 */
@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<User> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params == null) {
            return predicates;
        }
        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            String searchPattern = "%" + kw.toLowerCase().trim() + "%";
            Predicate matchUsername = b.like(b.lower(root.get("username")), searchPattern);
            Predicate matchEmail = b.like(b.lower(root.get("email")), searchPattern);
            Predicate matchFirstName = b.like(b.lower(root.get("firstName")), searchPattern);
            Predicate matchLastName = b.like(b.lower(root.get("lastName")), searchPattern);
            Predicate matchFullName = b.like(b.lower(root.get("fullName")), searchPattern);


            predicates.add(b.or(matchUsername, matchEmail, matchFirstName, matchLastName, matchFullName));
        }

        String role = params.get("role");
        if (role != null && !role.isEmpty()) {
            predicates.add(b.equal(root.get("role"), role));
        }
        
        String isInstructor = params.get("isInstructor");
        if (isInstructor != null && !isInstructor.isEmpty()) {
            predicates.add(b.equal(root.get("isInstructor"), Boolean.parseBoolean(isInstructor)));
        }
        
        return predicates;
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root);


        List<Predicate> predicates = buildPredicates(b, root, params);
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(b.desc(root.get("createdTime")));

        Query<User> query = session.createQuery(q);

        if (params != null && params.containsKey("page")) {
            int pageSize = this.env.getProperty("users.page_size", Integer.class);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * pageSize;

            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }
        return query.getResultList();
    }

    @Override
    public Long countUsers(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        return DaoUtils.count(session, User.class, (b, root) -> buildPredicates(b, root, params));
    }

    @Override
    public User getUserById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        Session session = this.factory.getObject().getCurrentSession();

        Query<User> q = session.createQuery("FROM User WHERE role = :role", User.class);
        q.setParameter("role", role);
        return q.getResultList();
    }

    @Override
    public User addUser(User user) {
        Session session = this.factory.getObject().getCurrentSession();
        
        user.setIsInstructor(false);
        user.setIsAdmin(false);
        user.setAuthProvider("LOCAL");
        user.setCreatedTime(new Date());
        session.persist(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Session session = this.factory.getObject().getCurrentSession();
        user.setUpdatedTime(new Date());
        return (User) session.merge(user);
    }

    @Override
    public boolean unActiveUser(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        User user = session.get(User.class, id);

        if (user != null) {
            user.setIsActive(false);
            session.merge(user);
            return true;
        }

        return false;
    }
    
    @Override
    public boolean activeUser(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        User user = session.get(User.class, id);

        if (user != null) {
            user.setIsActive(true);
            session.merge(user);
            return true;
        }

        return false;
    }

    @Override
    public boolean approveInstructor(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        User user = session.get(User.class, id);

        if (user != null && "INSTRUCTOR".equals(user.getRole())) {
            user.setIsInstructor(true);
            session.merge(user);
            return true;
        }
        return false;
    }

    @Override
    public User getUserByUsername(String username) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<User> q = session.createQuery("FROM User WHERE username = :username", User.class);
        q.setParameter("username", username);
        return q.uniqueResult();
    }
}
