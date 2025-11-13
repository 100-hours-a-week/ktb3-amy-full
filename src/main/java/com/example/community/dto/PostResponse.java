package com.example.community.dto;

import com.example.community.entity.PostEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    public static PostResponse of(PostEntity postEntity) {
        PostResponse response = new PostResponse();
        response.setId(postEntity.getPostId());
        response.setTitle(postEntity.getTitle());
        response.setContent(postEntity.getContent());
        response.setAuthorId(postEntity.getAuthor().getId());
        response.setAuthorNickname(postEntity.getAuthor().getNickname());
        response.setAuthorProfileImage(postEntity.getAuthor().getProfile_image());
        response.setCreatedAt(postEntity.getCreatedAt());
        response.setUpdatedAt(postEntity.getUpdatedAt());
        response.setCreatedBy(postEntity.getCreatedBy());
        response.setUpdatedBy(postEntity.getUpdatedBy());
        return response;
    }
}