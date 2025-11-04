package com.example.community.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostEntityTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void idTest() {
        PostEntity postEntity = new PostEntity("제목", "본문", "https://image.kr/post.png");
        entityManager.persist(postEntity);
    }

    @Test
    @Rollback(false)
    void createdUpdatedTest() {
        PostEntity postEntity = new PostEntity("제목", "본문", "https://image.kr/post.png");
        LocalDateTime now = LocalDateTime.now();

        postEntity.setCreatedAt(now);
        postEntity.setUpdatedAt(now);

        entityManager.persist(postEntity);
    }

    @Test
    @Rollback(false)
    void enumeratedTest() {
        PostEntity postEntity = new PostEntity("제목", "본문", "https://image.kr/post.png", PostType.NOTICE);
        entityManager.persist(postEntity);
    }

    @Test
    @Rollback(false)
    void flushTest() {
        PostEntity postEntity = new PostEntity("제목", "본문", "https://image.kr/post.png", PostType.NOTICE);

        System.out.println("=== Flush (아무것도 없음) ===");
        entityManager.flush(); // 아무것도 없음 (정상)
        System.out.println("========================");

        System.out.println("=== Persist ===");
        entityManager.persist(postEntity); // 영속화 (아직 INSERT 미발행)
        System.out.println("========================");

        System.out.println("=== Flush (INSERT 발생) ===");
        entityManager.flush(); // 여기서 INSERT 발생
        System.out.println("========================");
    }

    @Test
    @Rollback(false)
    void removeTest() {
        PostEntity postEntity = new PostEntity("제목", "본문", "https://image.kr/post.png", PostType.NOTICE);
        entityManager.persist(postEntity);

        entityManager.flush(); // INSERT 실행
        System.out.println("=== INSERT 쿼리 실행됨 ===");

        entityManager.remove(postEntity);
        System.out.println("=== remove 호출 (아직 DELETE 쿼리 안 나감) ===");

        entityManager.flush();
        System.out.println("=== DELETE 쿼리 실행됨 ===");
    }

    @Test
    @Rollback(false)
    void unidirectionalManyToOneTest() {
        // 유저 저장
        UserEntity userEntity = new UserEntity("tester@adapterz.kr", "123aS!", "tester", "https://image.kr/img.jpg", UserRole.ADMIN);
        entityManager.persist(userEntity);
        entityManager.flush();

        // 게시글 저장
        PostEntity postEntity = new PostEntity("공지 글", "내용", PostType.NOTICE, userEntity);
        entityManager.persist(postEntity);
        entityManager.flush();

        // 1차 캐시 초기화 후 조회
        entityManager.clear();
        PostEntity findPost = entityManager.find(PostEntity.class, postEntity.getPostId());
        System.out.println("findPost.getPostId() : " + findPost.getPostId());
        System.out.println("findPost.getTitle() : " + findPost.getTitle());
        System.out.println("findPost.getAuthor().getNickname() : " + findPost.getAuthor().getNickname());
    }

    @Test
    @Rollback(false)
    void bidirectionalOnetoManyTest() {
        // 유저 생성 및 저장
        UserEntity userEntity = new UserEntity("tester@adapterz.kr", "123aS!", "tester", "https://image.kr/img.jpg", UserRole.ADMIN);
        entityManager.persist(userEntity);
        // flush 시 INSERT 발생

        // 게시글 3개 생성
        PostEntity noticePost = new PostEntity("공지사항", "공지 내용", PostType.NOTICE, userEntity);
        PostEntity freePost1 = new PostEntity("자유게시판 글1", "자유 내용", PostType.FREE, userEntity);
        PostEntity freePost2 = new PostEntity("자유게시판 글2", "자유 내용", PostType.FREE, userEntity);

        // 연관관계 설정 (편의 메서드 사용)
        userEntity.addPost(noticePost);
        userEntity.addPost(freePost1);
        userEntity.addPost(freePost2);

        // 게시글 저장
        entityManager.persist(noticePost);
        entityManager.persist(freePost1);
        entityManager.persist(freePost2);
        // post.author 에 FK(user_id) 값이 들어가므로 persist 시 INSERT 쿼리에 포함됨

        // flush 로 DB 반영, clear 로 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        // 유저 다시 조회 후 posts 컬렉션으로 연관 게시글 확인
        UserEntity findUser = entityManager.find(UserEntity.class, userEntity.getId());
        System.out.println("조회된 유저 닉네임 = " + findUser.getNickname());
        System.out.println("연관된 게시글 수 = " + findUser.getPosts().size());
        findUser.getPosts().forEach(postEntity ->
                System.out.println("post.id = " + postEntity.getPostId() + ", title = " + postEntity.getTitle()));

        // 특정 게시글 다시 조회 후 author 로 유저 확인
        PostEntity findPost = entityManager.find(PostEntity.class, freePost1.getPostId());
        System.out.println("조회된 게시글 제목 = " + findPost.getTitle());
        System.out.println("작성자 닉네임 = " + findPost.getAuthor().getNickname());
    }

    @Test
    @Rollback(false)
    void elementCollectionTest() {
        // 작성자 생성 및 저장
        UserEntity userEntity = new UserEntity("tester@adapterz.kr", "123aS!", "tester", "https://image.kr/img.jpg", UserRole.ADMIN);
        entityManager.persist(userEntity);

        // 게시글 생성
        PostEntity postEntity = new PostEntity("태그 실습", "내용", PostType.FREE, userEntity);

        // 태그 추가
        postEntity.getTags().add(new Tag("Adaterz"));
        postEntity.getTags().add(new Tag("Startupcode"));
        postEntity.getTags().add(new Tag("Infinitybrain"));

        entityManager.persist(postEntity);

        entityManager.flush();
        entityManager.clear();

        PostEntity findPost = entityManager.find(PostEntity.class, postEntity.getPostId());
        System.out.println("게시글 제목 = " + findPost.getTitle());
        System.out.println("태그 개수 = " + findPost.getTags().size());
        findPost.getTags().forEach(tag ->
                System.out.println("태그 이름 = " + tag.getName())
        );
    }

    @Test
    @Rollback(false)
    void implicitJoinTest() {
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

        List<String> result = entityManager.createQuery(
                "select p.author.nickname from PostEntity p",
                String.class
        ).getResultList();

        System.out.println("닉네임 목록 = " + result);
    }

    @Test
    @Rollback(false)
    void explicitJoinTest() {
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

        List<PostEntity> result = entityManager.createQuery(
                        "select p from UserEntity u join u.posts p where u.nickname = :nickname",
                        PostEntity.class
                ).setParameter("nickname", "Adapterz1")
                .getResultList();

        System.out.println("Adapterz1의 게시글 수(명시적 조인) = " + result.size());
    }
}