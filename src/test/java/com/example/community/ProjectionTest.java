package com.example.community;

import com.example.community.dto.UserProjectionDto;
import com.example.community.entity.ProfileInfo;
import com.example.community.entity.UserEntity;
import com.example.community.entity.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class ProjectionTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void entityProjectionTest() {
        // 5개의 유저 더미 데이터 추가
        for (int i = 1; i <= 5; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER,
                    new ProfileInfo("http://img/" + i + ".png", "Hello " + i)
            );
            userEntity.setCreatedAt(LocalDateTime.now());
            entityManager.persist(userEntity);
        }
        entityManager.flush();
        entityManager.clear();

        List<UserEntity> findUsers = entityManager
                .createQuery("select u from UserEntity u order by u.id", UserEntity.class)
                .getResultList();

        findUsers.forEach(u ->
                System.out.println("id=" + u.getId() + ", email=" + u.getEmail() + ", nick=" + u.getNickname())
        );

        findUsers.getFirst().setNickname("New Nickname");
        System.out.println("=== 트랜잭션 커밋 시 UPDATE 자동 반영 ===");
    }

    @Test
    @Rollback(false)
    void embeddedProjectionTest() {
        // 5개의 유저 더미 데이터 추가
        for (int i = 1; i <= 5; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER,
                    new ProfileInfo("http://img/" + i + ".png", "Hello " + i)
            );
            userEntity.setCreatedAt(LocalDateTime.now());
            entityManager.persist(userEntity);
        }

        List<ProfileInfo> findProfiles = entityManager
                .createQuery("select u.profileInfo from UserEntity u where u.profileInfo is not null", ProfileInfo.class)
                .getResultList();

        findProfiles.forEach(p ->
                System.out.println("imageUrl=" + p.getImageUrl() + ", intro=" + p.getIntroduction())
        );
    }

    @Test
    @Rollback(false)
    void scalarProjectionWithTupleTest() {
        // 5개의 유저 더미 데이터 추가
        for (int i = 1; i <= 5; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER,
                    new ProfileInfo("http://img/" + i + ".png", "Hello " + i)
            );
            userEntity.setCreatedAt(LocalDateTime.now());
            entityManager.persist(userEntity);

            List<Tuple> findUsers = entityManager
                    .createQuery(
                            "select u.email as email, u.nickname as nickname, u.createdAt as createdAt " +
                                    "from UserEntity u order by u.id", Tuple.class)
                    .getResultList();

            findUsers.forEach(t ->
                    System.out.println("email=" + t.get("email") +
                            ", nickname=" + t.get("nickname") +
                            ", createdAt=" + t.get("createdAt"))
            );
        }
    }

    @Test
    @Rollback(false)
    void scalarProjectionWithObjectTest() {
        // 5개의 유저 더미 데이터 추가
        for (int i = 1; i <= 5; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER,
                    new ProfileInfo("http://img/" + i + ".png", "Hello " + i)
            );
            userEntity.setCreatedAt(LocalDateTime.now());
            entityManager.persist(userEntity);
        }

        List<Object[]> findUsers = entityManager
                .createQuery("select u.email, u.nickname from UserEntity u order by u.id", Object[].class)
                .getResultList();

        findUsers.forEach(arr ->
                System.out.println("email=" + arr[0] + ", nickname=" + arr[1])
        );
    }

    @Test
    @Rollback(false)
    void projectionWithDtoTest() {
        // 5개의 유저 더미 데이터 추가
        for (int i = 1; i <= 5; i++) {
            UserEntity userEntity = new UserEntity(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER,
                    new ProfileInfo("http://img/" + i + ".png", "Hello " + i)
            );
            userEntity.setCreatedAt(LocalDateTime.now());
            entityManager.persist(userEntity);
        }

        List<UserProjectionDto> findUsers = entityManager
                .createQuery(
                        "select new com.example.community.dto.UserProjectionDto(u.email, u.nickname) " +
                                "from UserEntity u order by u.id",
                        UserProjectionDto.class)
                .getResultList();

        findUsers.forEach(System.out::println);
    }

}