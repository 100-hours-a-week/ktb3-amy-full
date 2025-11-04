package com.example.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    private String content;

    @Enumerated(EnumType.STRING)
    private CommentRole commentRole;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected CommentEntity() {}

    public CommentEntity(String content) {
        this.content = content;
    }

    public CommentEntity(String content, CommentRole commentRole) {
        this.content = content;
        this.commentRole = commentRole;
    }
}