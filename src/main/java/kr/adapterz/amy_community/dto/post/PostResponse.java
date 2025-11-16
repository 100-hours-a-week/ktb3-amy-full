package kr.adapterz.amy_community.dto.post;

import kr.adapterz.amy_community.entity.PostEntity;
import lombok.Getter;

@Getter
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private int likes;
    private int views;
    private int commentCount;

    private String imageUrl;
    private String authorNickname;

    public PostResponse(PostEntity post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.likes = post.getLikes();
        this.views = post.getViews();
        this.commentCount = post.getCommentCount();

        if (post.getImage() != null) {
            this.imageUrl = "data:image/png;base64," + post.getImage();
        } else {
            this.authorNickname = "익명";
        }
    }
}
