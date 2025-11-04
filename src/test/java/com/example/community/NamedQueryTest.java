package com.example.community;

import com.example.community.entity.PostEntity;
import com.example.community.entity.PostType;
import com.example.community.entity.UserEntity;
import com.example.community.entity.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class NamedQueryTest {
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void namedQueryTest() {
        // 유저 더미 데이터 생성
        UserEntity user1 = new UserEntity("master@adapterz.kr", "pw", "Master", "http://example.com/image.png", UserRole.ADMIN);
        UserEntity user2 = new UserEntity("tester@adapterz.kr", "pw", "Tester", "http://example.com/image.png", UserRole.USER);
        entityManager.persist(user1);
        entityManager.persist(user2);

        // 게시글 더미 데이터 생성
        entityManager.persist(new PostEntity("공지사항", "내용", PostType.NOTICE, user1));
        entityManager.persist(new PostEntity("규칙안내", "내용", PostType.FREE, user1));
        entityManager.persist(new PostEntity("안녕하세요", "내용", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        // UserEntity.findByEmail
        UserEntity findByEmailResult = entityManager
                .createNamedQuery("UserEntity.findByEmail", UserEntity.class)
                .setParameter("email", "tester@adapterz.kr")
                .getSingleResult();
        System.out.println("findByEmail : " + findByEmailResult.getNickname());

        // User.searchByNickname
        List<UserEntity> searchByNicknameResult = entityManager
                .createNamedQuery("UserEntity.searchByNickname", UserEntity.class)
                .setParameter("keyword", "Tester")
                .getResultList();
        searchByNicknameResult.forEach(u -> System.out.println("searchByNickname : " + u.getEmail() + " / " + u.getNickname()));

        // Post.findByAuthorId
        List<PostEntity> findByAuthorIdResult = entityManager
                .createNamedQuery("PostEntity.findByAuthorId", PostEntity.class)
                .setParameter("authorId", user2.getId())
                .getResultList();
        System.out.println("PostEntity.findByAuthorId size = " + findByAuthorIdResult.size());
        findByAuthorIdResult.forEach(p -> System.out.println("postEntity : " + p.getTitle()));

        // Post.titlesByAuthorNickname
        List<String> titlesByAuthorNicknameResult = entityManager
                .createNamedQuery("PostEntity.titlesByAuthorNickname", String.class)
                .setParameter("nickname", "Master")
                .getResultList();
        titlesByAuthorNicknameResult.forEach(t -> System.out.println("titlesByAuthorNickname : " + t));

        // Post.countByAuthorId
        Long countByAuthorIdResult = entityManager
                .createNamedQuery("PostEntity.countByAuthorId", Long.class)
                .setParameter("authorId", user1.getId())
                .getSingleResult();
        System.out.println("PostEntity.countByAuthorId : " + countByAuthorIdResult);
    }
}