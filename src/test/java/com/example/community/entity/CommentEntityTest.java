package com.example.community.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentEntityTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void idTest() {
        CommentEntity commentEntity = new CommentEntity("댓글 내용");
        entityManager.persist(commentEntity);
    }

    @Test
    @Rollback(false)
    void createdUpdatedAtTest() {
        CommentEntity commentEntity = new CommentEntity("댓글 내용");
        LocalDateTime now = LocalDateTime.now();

        commentEntity.setCreatedAt(now);
        commentEntity.setUpdatedAt(now);

        entityManager.persist(commentEntity);
    }

    @Test
    @Rollback(false)
    void enumeratedTest() {
        CommentEntity commentEntity = new CommentEntity("댓글 내용", CommentRole.ADMIN);
        entityManager.persist(commentEntity);
    }

    @Test
    @Rollback(false)
    void flushTest() {
        CommentEntity commentEntity = new CommentEntity("댓글 내용", CommentRole.ADMIN);

        System.out.println("=== Flush (아무것도 없음) ===");
        entityManager.flush(); // 아무것도 없음 (정상)
        System.out.println("========================");

        System.out.println("=== Persist ===");
        entityManager.persist(commentEntity); // 영속화 (아직 INSERT 미발행)
        System.out.println("========================");

        System.out.println("=== Flush (INSERT 발생) ===");
        entityManager.persist(commentEntity); // 여기서 INSERT 발생
        System.out.println("========================");
    }

    @Test
    @Rollback(false)
    void removeTest() {
        CommentEntity commentEntity = new CommentEntity("댓글 내용", CommentRole.COMMENT);
        entityManager.persist(commentEntity);

        entityManager.flush(); //INSERT 실행
        System.out.println("=== INSERT 쿼리 실행됨 ===");

        entityManager.remove(commentEntity);
        System.out.println("=== remove 호출 (아직 DELETE 쿼리 안 나감) ===");

        entityManager.flush();
        System.out.println("=== DELETE 쿼리 실행됨 ===");
    }
}