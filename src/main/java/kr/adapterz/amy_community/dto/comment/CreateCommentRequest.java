package kr.adapterz.amy_community.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateCommentRequest {
    private Long postId;
    private Long authorId;
    private String content;
}
