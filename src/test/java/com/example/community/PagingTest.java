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
public class PagingTest {
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void pagingTest() {
        UserEntity masterUser = new UserEntity("master@adapterz.kr", "pw", "Master", "http://example.com/image.png", UserRole.ADMIN);
        UserEntity userUser = new UserEntity("tester@adapterz.kr", "pw", "Tester", "http://example.com/image.png", UserRole.USER);
        entityManager.persist(masterUser);
        entityManager.persist(userUser);

        for (int i = 1; i <= 100; i++) {
            UserEntity author = (i % 2 == 0) ? masterUser : userUser;
            PostType type = (i % 4 == 0) ? PostType.FREE : PostType.NOTICE;
            entityManager.persist(new PostEntity("제목" + i, "내용" + i, type, author));
        }

        entityManager.flush();
        entityManager.clear();

        int page = 0;  // 0부터 시작: 0, 1, 2 ...
        int size = 10; // 페이지 크기
        int offset = page * size;

        // 안정적인 페이징을 위해 정렬 필수
        List<PostEntity> pageData = entityManager.createQuery("select p from PostEntity p order by p.id", PostEntity.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        Long total = entityManager.createQuery("select count(p) from PostEntity p", Long.class)
                .getSingleResult();

        int totalPages = (int) Math.ceil(total / (double) size);

        System.out.println("current page = " + page);
        System.out.println("page size = " + size);
        System.out.println("total elements = " + total);
        System.out.println("total pages = " + totalPages);

        pageData.forEach(p ->
                System.out.println("postId=" + p.getPostId() + ", title=" + p.getTitle())
        );
    }

    @Test
    @Rollback(false)
    void pagingWithFetchJoinTest() {
        UserEntity masterUser = new UserEntity("master@adapterz.kr", "pw", "Master", "http://example.com/imageA.png", UserRole.ADMIN);
        UserEntity userUser = new UserEntity("tester@adapterz.kr", "pw", "Tester", "http://example.com/imageB.png", UserRole.USER);
        entityManager.persist(masterUser);
        entityManager.persist(userUser);

        for (int i = 1; i <= 100; i++) {
            UserEntity author = (i % 2 == 0) ? masterUser : userUser;
            PostType type = (i % 4 == 0) ? PostType.FREE : PostType.NOTICE;
            entityManager.persist(new PostEntity("제목" + i, "내용" + i, type, author));
        }

        entityManager.flush();
        entityManager.clear();

        int page = 0;
        int size = 5;
        int offset = page * size;

        // ManyToOne fetch join 은 페이징과 함께 사용 가능
        List<PostEntity> result = entityManager.createQuery(
                        "select p from PostEntity p join fetch p.author order by p.id", PostEntity.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        // author 접근 시 추가 쿼리 없음
        result.forEach(p ->
                System.out.println("postId=" + p.getPostId()
                        + ", title=" + p.getTitle()
                        + ", author.Nickname=" + p.getAuthor().getNickname())
        );
    }
}