/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lmk.repositories.impl;

import com.lmk.pojo.User;
import com.lmk.repositories.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<User> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params == null) {
            return predicates;
        }

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            String pattern = "%" + kw.toLowerCase().trim() + "%";
            predicates.add(b.or(
                    b.like(b.lower(root.get("username")), pattern),
                    b.like(b.lower(root.get("email")), pattern),
                    b.like(b.lower(root.get("firstName")), pattern),
                    b.like(b.lower(root.get("lastName")), pattern)
            ));
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
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root);

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        
        q.orderBy(b.desc(root.get("createdTime")));

        Query<User> query = s.createQuery(q);

        if (params != null) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int pageSize = this.env.getProperty("users.page_size", Integer.class, 1);
            int start = (page - 1) * pageSize;

            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }

    @Override
    public User getUserById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(User.class, id);
    }

    @Override
    public User getUserByUsername(String username) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root).where(b.equal(root.get("username"), username));
        return s.createQuery(q).uniqueResult();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root).where(b.equal(root.get("role"), role));
        return s.createQuery(q).getResultList();
    }

    @Override
    public void saveUser(User u) {
        Session session = this.factory.getObject().getCurrentSession();
        if (u.getId() == null) {
            session.persist(u);
        } else {
            session.merge(u);
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }
}
