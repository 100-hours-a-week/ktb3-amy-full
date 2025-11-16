package kr.adapterz.amy_community.dto.comment;

import kr.adapterz.amy_community.entity.CommentEntity;
import lombok.Getter;

@Getter
public class CommentResponse {
    private Long id;
    private Long postId;
    private String content;
    private String authorNickname;
    private String profileImage;
    private String createdAt;

    public CommentResponse(CommentEntity c) {
        this.id = c.getId();
        this.postId = c.getPost().getId();
        this.content = c.getContent();
        this.authorNickname = c.getAuthor().getNickname();
        this.profileImage = c.getAuthor().getProfileImage();
        this.createdAt = c.getCreatedAt();
    }
}
