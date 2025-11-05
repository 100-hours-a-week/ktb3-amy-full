package com.example.community.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public long countUsersByNicknameContains(String keyword) {
        return entityManager.createQuery(
                        "select count(u) from UserEntity u " +
                                "where lower(u.nickname) like lower(concat('%', :keyword, '%'))", Long.class)
                .setParameter("keyword", keyword)
                .getSingleResult();
    }
}
