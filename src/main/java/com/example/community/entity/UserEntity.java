package com.example.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        // 이메일로 단건 조회
        @NamedQuery(
                name = "UserEntity.findByEmail",
                query = "select u from UserEntity u where u.email = :email"
        ),
        // 닉네임 like 검색
        @NamedQuery(
                name = "UserEntity.searchByNickname",
                query = "select u from UserEntity u where u.nickname like concat('%', :keyword, '%')"
        )
})
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String password;

    @Transient
    private String passwordConfirm;

    private String nickname;
    private String profile_image;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "author")
    private List<PostEntity> posts = new ArrayList<>();

    @Embedded
    private ProfileInfo profileInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected UserEntity() {}

    public UserEntity(String email, String password, String nickname, String profile_image) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_image = profile_image;
    }

    public UserEntity(String email, String password, String nickname, String profile_image, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profile_image = profile_image;
        this.userRole = userRole;
    }

    public UserEntity(String email, String password, String passwordConfirm, String nickname, String profile_image, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
        this.profile_image = profile_image;
        this.userRole = userRole;
    }

    public UserEntity(String email, String password, String nickname, UserRole userRole, ProfileInfo profileInfo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
        this.profileInfo = profileInfo;
    }


    // 편의 메서드: 양쪽 동기화
    public void addPost(PostEntity postEntity) {
        this.posts.add(postEntity);
        postEntity.setAuthor(this);
    }

    public void removePost(PostEntity postEntity) {
        this.posts.remove(postEntity);
        postEntity.setAuthor(null);
    }
}