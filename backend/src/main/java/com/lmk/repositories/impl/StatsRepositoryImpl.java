package com.lmk.repositories.impl;

import com.lmk.pojo.Course;
import com.lmk.pojo.Payment;
import com.lmk.pojo.User;
import com.lmk.repositories.StatsRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class StatsRepositoryImpl implements StatsRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Long countAllCourses() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<com.lmk.pojo.Course> root = q.from(com.lmk.pojo.Course.class);

        q.select(b.count(root));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Long countAllEnrollments() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<com.lmk.pojo.Enrollment> root = q.from(com.lmk.pojo.Enrollment.class);

        q.select(b.count(root));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Double sumSuccessPayments() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Double> q = b.createQuery(Double.class);
        Root<Payment> root = q.from(Payment.class);

        q.select(b.sum(root.get("amount")).as(Double.class))
                .where(b.equal(root.get("status"), "SUCCESS"));

        Double result = s.createQuery(q).getSingleResult();
        return result != null ? result : 0.0;
    }

    @Override
    public List<Object[]> countUsersByRole() {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root<User> root = q.from(User.class);

        q.multiselect(root.get("role"), b.count(root)).groupBy(root.get("role"));
        return s.createQuery(q).getResultList();
    }

    @Override
    public Long countCoursesByInstructor(int instructorId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Course> root = q.from(Course.class);

        q.select(b.count(root)).where(b.equal(root.get("instructorId").get("id"), instructorId));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public Long countEnrollmentsByInstructor(int instructorId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<com.lmk.pojo.Enrollment> root = q.from(com.lmk.pojo.Enrollment.class);

        q.select(b.count(root))
                .where(b.equal(root.get("courseId").get("instructorId").get("id"), instructorId));
        return s.createQuery(q).getSingleResult();
    }

    @Override
    public List<Object[]> getRevenuePerCourseByInstructor(int instructorId) {
        Session s = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);

        Root<Payment> root = q.from(Payment.class);
        Join<Object, Object> enrollJoin = root.join("enrollmentId");
        Join<Object, Object> courseJoin = enrollJoin.join("courseId");

        q.multiselect(
                courseJoin.get("subject"),
                b.countDistinct(enrollJoin.get("id")),
                b.sum(root.get("amount"))
        ).where(
                b.equal(root.get("status"), "SUCCESS"),
                b.equal(courseJoin.get("instructorId").get("id"), instructorId)
        ).groupBy(
                courseJoin.get("id"),
                courseJoin.get("subject")
        );

        return s.createQuery(q).getResultList();
    }

    @Override
    public List<Object[]> getMonthlyRevenue(int year) {
        Session s = factory.getObject().getCurrentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = b.createQuery(Object[].class);
        Root<Payment> root = q.from(Payment.class);

        q.multiselect(
                b.function("MONTH", Integer.class, root.get("paidTime")),
                b.sum(root.get("amount"))
        ).where(
                b.equal(root.get("status"), "SUCCESS"),
                b.equal(b.function("YEAR", Integer.class, root.get("paidTime")), year)
        ).groupBy(
                b.function("MONTH", Integer.class, root.get("paidTime"))
        );

        return s.createQuery(q).getResultList();
    }

}
