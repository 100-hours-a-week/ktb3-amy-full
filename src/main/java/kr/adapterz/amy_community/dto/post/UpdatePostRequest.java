package kr.adapterz.amy_community.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePostRequest {
    private String title;
    private String content;
    private String imageUrl;
}