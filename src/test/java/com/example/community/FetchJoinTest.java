package com.example.community;

import com.example.community.entity.PostEntity;
import com.example.community.entity.PostType;
import com.example.community.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class FetchJoinTest {
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void nPlusOneProblemTest() {
        // 3개의 유저 및 게시글 더미 데이터 추가
        for (int i = 1; i <= 3; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    "https://image.kr/img.jpg" + i
            );
            entityManager.persist(userEntity);

            PostEntity postEntity = new PostEntity(
                    "test title" + i,
                    "test content" + i,
                    PostType.NOTICE,
                    userEntity
            );
            entityManager.persist(postEntity);
        }

        entityManager.flush();
        entityManager.clear();

        // 모든 User를 한 번에 조회
        List<UserEntity> findAllUsers = entityManager.createQuery("select u from UserEntity u", UserEntity.class)
                .getResultList();

        // 각 User의 posts 접근 시점에 LAZY 로딩으로 추가 SELECT 발생
        for (UserEntity findUserEntity : findAllUsers) {
            int size = findUserEntity.getPosts().size(); // 여기서 사용자 별로 select 문 날라감
            System.out.println("findUser.getNickname() = " + findUserEntity.getNickname() + ", posts.size() = " + size);
        }
    }

    @Test
    @Rollback(false)
    void solvedWithFetchJoinTest() {
        // 3개의 유저 및 게시글 더미 데이터 추가
        for (int i = 1; i <= 3; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    "https://image.kr/img.jpg" + i
            );
            entityManager.persist(userEntity);

            PostEntity postEntity = new PostEntity(
                    "test title" + i,
                    "test content" + i,
                    PostType.NOTICE,
                    userEntity
            );
            entityManager.persist(postEntity);
        }

        entityManager.flush();
        entityManager.clear();

        // 모든 User를 한 번에 조회 (fetch join 사용)
        List<UserEntity> findAllUsers = entityManager.createQuery("select u from UserEntity u join fetch u.posts", UserEntity.class)
                .getResultList();

        // posts 접근 시 추가 쿼리 없음
        for (UserEntity findUserEntity : findAllUsers) {
            int size = findUserEntity.getPosts().size(); // 여기서 사용자 별로 select 문 날라감
            System.out.println("findUserEntity.getNickname() = " + findUserEntity.getNickname() + ", posts.size() = " + size);
        }
    }
}