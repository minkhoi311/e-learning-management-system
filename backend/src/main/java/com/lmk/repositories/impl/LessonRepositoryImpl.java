package com.lmk.repositories.impl;

import com.lmk.pojo.Lesson;
import com.lmk.repositories.LessonRepository;
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
public class LessonRepositoryImpl implements LessonRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    // Hàm build điều kiện lọc động cho Lesson
    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<Lesson> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params == null) {
            return predicates;
        }

        // 1. Tìm kiếm theo tên bài học (subject)
        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            predicates.add(b.like(b.lower(root.get("subject")), "%" + kw.toLowerCase().trim() + "%"));
        }

        // 2. Lọc bài học theo Khóa học (courseId)
        String courseId = params.get("courseId");
        if (courseId != null && !courseId.isEmpty()) {
            predicates.add(b.equal(root.get("courseId").as(Integer.class), Integer.parseInt(courseId)));
        }

        // 3. Lọc theo trạng thái hoạt động (isActive)
        String active = params.get("isActive");
        if (active != null && !active.isEmpty()) {
            predicates.add(b.equal(root.get("isActive"), Boolean.parseBoolean(active)));
        }

        return predicates;
    }

    @Override
    public List<Lesson> getLessons(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Lesson> q = b.createQuery(Lesson.class);
        Root<Lesson> root = q.from(Lesson.class);
        q.select(root);

        // Áp dụng bộ lọc
        List<Predicate> predicates = buildPredicates(b, root, params);
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        // Áp dụng sắp xếp
        if (params != null) {
            String sort = params.getOrDefault("sort", "newest");
            switch (sort) {
                case "name_asc" ->
                    q.orderBy(b.asc(root.get("subject")));
                case "name_desc" ->
                    q.orderBy(b.desc(root.get("subject")));
                case "oldest" ->
                    q.orderBy(b.asc(root.get("createdTime")));
                default ->
                    q.orderBy(b.desc(root.get("createdTime"))); // Mặc định mới nhất lên đầu
            }
        }

        Query<Lesson> query = session.createQuery(q);

        // Áp dụng phân trang
        if (params != null && params.containsKey("page")) {
            // Lấy size từ file configs.properties, nếu không có thì mặc định là 6
            int pageSize = Integer.parseInt(this.env.getProperty("lessons.page_size", "6"));
            int page = Integer.parseInt(params.getOrDefault("page", "1"));
            int start = (page - 1) * pageSize;

            query.setMaxResults(pageSize);
            query.setFirstResult(start);
        }

        return query.getResultList();
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
            session.merge(lesson); // Dùng merge cho thao tác cập nhật
        } else {
            session.persist(lesson); // Dùng persist cho thao tác thêm mới
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

    @Override
    public Long countLesson(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();

        return DaoUtils.count(session, Lesson.class, (b, root) -> buildPredicates(b, root, params));
    }
    
}
