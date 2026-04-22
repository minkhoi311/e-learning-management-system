package com.lmk.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.util.List;

public class DaoUtils {

    /**
     * Interface giúp truyền logic build điều kiện (WHERE) từ Repository vào Utils
     */
    @FunctionalInterface
    public interface CustomPredicate<T> {
        List<Predicate> build(CriteriaBuilder b, Root<T> root);
    }

    public static <T> Long count(Session session, Class<T> entityClass, CustomPredicate<T> customPredicate) {
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<T> root = q.from(entityClass);

        q.select(b.count(root));

        // Nếu có truyền logic filter vào thì áp dụng
        if (customPredicate != null) {
            List<Predicate> predicates = customPredicate.build(b, root);
            if (predicates != null && !predicates.isEmpty()) {
                q.where(predicates.toArray(Predicate[]::new));
            }
        }

        return session.createQuery(q).getSingleResult();
    }

    /**
     * Hàm tính tổng số trang (Dùng ở Controller để đẩy ra View)
     */
    public static int calculateTotalPages(Long totalItems, int pageSize) {
        return (int) Math.ceil((double) totalItems / pageSize);
    }
}