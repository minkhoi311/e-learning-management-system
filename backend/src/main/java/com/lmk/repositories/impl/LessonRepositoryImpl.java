package com.lmk.repositories.impl;

import com.lmk.pojo.Lesson;
import com.lmk.repositories.LessonRepository;
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
public class LessonRepositoryImpl implements LessonRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<Lesson> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params == null) return predicates;

        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty())
            predicates.add(b.like(b.lower(root.get("subject")), "%" + kw.toLowerCase().trim() + "%"));

        String courseId = params.get("courseId");
        if (courseId != null && !courseId.isEmpty())
            predicates.add(b.equal(root.get("courseId").as(Integer.class), Integer.parseInt(courseId)));

        String isActive = params.get("isActive");
        if (isActive != null && !isActive.isEmpty())
            predicates.add(b.equal(root.get("isActive"), Boolean.parseBoolean(isActive)));

        return predicates;
    }

    @Override
    public List<Lesson> getLessons(Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Lesson> q = b.createQuery(Lesson.class);
        Root<Lesson> root = q.from(Lesson.class);
        q.select(root);

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (!predicates.isEmpty())
            q.where(predicates.toArray(Predicate[]::new));

        q.orderBy(b.asc(root.get("orderIndex")));

        Query<Lesson> query = s.createQuery(q);

        if (params != null) {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int pageSize = this.env.getProperty("lessons.page_size", Integer.class, 1);
            int start = (page - 1) * pageSize;

            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }

        return query.getResultList();
    }
    
     @Override
    public Long countLessonsByCourseId(int courseId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Lesson> root = q.from(Lesson.class);
        q.select(b.count(root)).where(b.equal(root.get("courseId").get("id"), courseId));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Lesson getLessonById(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(Lesson.class, id);
    }

    @Override
    public void addOrUpdateLesson(Lesson lesson) {
        Session session = this.factory.getObject().getCurrentSession();
        if (lesson.getId() != null) {
            session.merge(lesson);
        } else {
            session.persist(lesson);
        }
    }

    @Override
    public void deleteLesson(int id) {
        Session session = this.factory.getObject().getCurrentSession();
        Lesson lesson = session.get(Lesson.class, id);
        if (lesson != null) {
            session.remove(lesson);
        }
    }
}
