package com.example.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@NamedQueries({
        // 작성자 ID로 게시글 목록
        @NamedQuery(
                name = "PostEntity.findByAuthorId",
                query = "select p from PostEntity p where p.author.id = :authorId"
        ),
        // 작성자 닉네임으로 제목 목록
        @NamedQuery(
                name = "PostEntity.titlesByAuthorNickname",
                query = "select p.title from PostEntity p where p.author.nickname = :nickname"
        ),
        // 작성자별 게시글 수 집계
        @NamedQuery(
                name = "PostEntity.countByAuthorId",
                query = "select count(p) from PostEntity p where p.author.id = :authorId"
        )
})
@Getter
@Setter
public class PostEntity extends AbstractAuditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    private String title;
    private String content;
    private String image_url;

    private int likes = 0;

    @Enumerated(EnumType.STRING)
    private PostType postType = PostType.FREE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK(postEntity.user_id) → userEntity.user_id
    private UserEntity author;

    @ElementCollection
    @CollectionTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Tag> tags = new HashSet<>();

    protected PostEntity() {
    }

    public PostEntity(String title, String content, String image_url) {
        this.title = title;
        this.content = content;
        this.image_url = image_url;
    }

    public PostEntity(String title, String content, String image_url, PostType postType) {
        this.title = title;
        this.content = content;
        this.image_url = image_url;
        this.postType = postType;
    }

    public PostEntity(String title, String content, PostType postType, UserEntity author) {
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.author = author;
    }

    public PostEntity(String title, String content, UserEntity author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}