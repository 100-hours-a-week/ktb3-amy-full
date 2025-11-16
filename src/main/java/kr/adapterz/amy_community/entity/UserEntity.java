package kr.adapterz.amy_community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;       // unique 아님 (예외 처리 안 쓰기 때문)
    private String password;
    private String nickname;

    @Column(name = "profile_image", columnDefinition = "LONGTEXT")
    private String profileImage;
}