package kr.adapterz.amy_community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "post")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    // 서비스/컨트롤러 코드와 일치되도록 필드명 변경
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    // author 안 쓸거면 제거 또는 그대로 둬도 null 허용
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "author_id")
    // private UserEntity author;

    private int views = 0;
    private int likes = 0;
    private int commentCount = 0;

    // createdAt 도 일단 프론트 요구사항대로 String 사용
    private String createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
}
