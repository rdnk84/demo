package com.example.demo.model.db.repository;

import com.example.demo.model.db.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserSearchDao {

    private final EntityManager entityManager;

    public List<User> usersByQuery(String firstname, String lastname, String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        //select * from user
        Root<User> root = criteriaQuery.from(User.class);

        //prepare WHERE clause
        //ex. WHERE firstname like '%Ann%'
        Predicate firstnamePredicate = criteriaBuilder.like(root.get("firstname"), "%" + firstname + "%");
        Predicate lastnamePredicate = criteriaBuilder.like(root.get("lastname"), "%" + lastname + "%");
        Predicate emailPredicate = criteriaBuilder.like(root.get("email"), "%" + email + "%");
        Predicate firstnameOrLastnamePredicate = criteriaBuilder.or(firstnamePredicate, lastnamePredicate);

        //=>final query ==> select * from users where firstname like '%ann%' or lastname like '%ann%' and email like '%em%'
        Predicate firstnameOrLastNameAndEmail = criteriaBuilder.and(firstnameOrLastnamePredicate, emailPredicate);
        criteriaQuery.where(firstnameOrLastNameAndEmail);
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public Page<User> usersByCriteria(Pageable pageRequest, String firstname, String lastname, String email) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        List<Predicate> predicates = new ArrayList<>();
        //select * from user
        Root<User> root = criteriaQuery.from(User.class);

        if (StringUtils.isNotBlank(firstname)) {
            Predicate firstnamePredicate = criteriaBuilder.like(root.get("firstName"), "%" + firstname + "%");
            predicates.add(firstnamePredicate);
        }
        if (StringUtils.isNotBlank(lastname)) {
            Predicate lastnamePredicate = criteriaBuilder.like(root.get("lastName"), "%" + lastname + "%");
            predicates.add(lastnamePredicate);
        }
        if (StringUtils.isNotBlank(email)) {
            Predicate emailPredicate = criteriaBuilder.like(root.get("email"), "%" + email + "%");
            predicates.add(emailPredicate);
        }
        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);

        List<User> result = query.getResultList();
        return new PageImpl<>(result, pageRequest, result.size());
    }
}
