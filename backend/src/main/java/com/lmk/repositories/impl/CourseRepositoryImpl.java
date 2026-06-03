package com.lmk.repositories.impl;

import com.lmk.pojo.Course;
import com.lmk.repositories.CourseRepository;
import com.lmk.utils.DaoUtils;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@PropertySource("classpath:configs.properties")
@Transactional
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<Course> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params == null) {
            return predicates;
        }

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            predicates.add(b.like(b.lower(root.get("subject")), "%" + kw.toLowerCase().trim() + "%"));
        }

        String cateId = params.get("cateId");
        if (cateId != null && !cateId.isEmpty()) {
            predicates.add(b.equal(root.get("categoryId").as(Integer.class), cateId));
        }

        // Lọc từ giá
        String fromPrice = params.get("fromPrice");
        if (fromPrice != null && !fromPrice.isEmpty()) {
            predicates.add(b.greaterThanOrEqualTo(root.get("price"), Double.parseDouble(fromPrice)));
        }

        // Lọc đến giá
        String toPrice = params.get("toPrice");
        if (toPrice != null && !toPrice.isEmpty()) {
            predicates.add(b.lessThanOrEqualTo(root.get("price"), Double.parseDouble(toPrice)));
        }

        // Lọc theo trạng thái (isActive)
        String active = params.get("isActive");
        if (active != null && !active.isEmpty()) {
            predicates.add(b.equal(root.get("isActive"), Boolean.parseBoolean(active)));
        }

        String username = params.get("username");
        if (username != null && !username.isEmpty()) {
            Predicate pUsername = b.equal(root.get("instructorId").get("username"), username);
            predicates.add(pUsername);
        }

        return predicates;
    }

    @Override
    public List<Course> getCourses(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);
        Root root = q.from(Course.class);
        q.select(root);

        // Áp dụng bộ lọc
        List<Predicate> predicates = buildPredicates(b, root, params);
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        // Sắp xếp
        if (params != null) {
            String sort = params.getOrDefault("sort", "newest");
            switch (sort) {
                case "price_asc" ->
                    q.orderBy(b.asc(root.get("price")));
                case "price_desc" ->
                    q.orderBy(b.desc(root.get("price")));
                case "name_asc" ->
                    q.orderBy(b.asc(root.get("subject")));
                default ->
                    q.orderBy(b.desc(root.get("createdTime")));
            }
        }

        Query<Course> query = session.createQuery(q);

        if (params != null && params.containsKey("page")) {
            int pageSize = this.env.getProperty("courses.page_size", Integer.class);
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * pageSize;

            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }
        return query.getResultList();

    }

    @Override
    public Course getCourseById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Course.class, id);
    }

    @Override
    public List<Course> getCoursesByIds(List<Integer> ids) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Course> q = b.createQuery(Course.class);

        Root<Course> root = q.from(Course.class);

        q.select(root);

        q.where(root.get("id").in(ids));

        Query<Course> query = session.createQuery(q);

        return query.getResultList();
    }

    @Override
    public void addOrUpdateCourse(Course c) {
        Session session = this.factory.getObject().getCurrentSession();
        if (c.getId() != null) {
            session.merge(c);
        } else {
            session.persist(c);
        }
    }

    @Override
    public boolean deleteCourse(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        Course course = session.get(Course.class, id);
        if (course == null) {
            return false;
        }
        session.remove(course);
        return true;
    }

    @Override
    public Long countCourse(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();

        return DaoUtils.count(session, Course.class, (b, root) -> buildPredicates(b, root, params));
    }

}
