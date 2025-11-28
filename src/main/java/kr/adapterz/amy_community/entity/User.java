package kr.adapterz.amy_community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 기본 회원 정보
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    // 권한
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    // 프로필 이미지
    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl = "/images/default-profile.png";

    // Refresh Token
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    // 연관관계
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Post> posts = new ArrayList<>();

    // Builder
    @Builder
    public User(String email, String password, String nickname,
                String profileImageUrl, UserRole role) {

        this.email = email;
        this.password = password;
        this.nickname = nickname;

        this.profileImageUrl = profileImageUrl != null
                ? profileImageUrl
                : "/images/default-profile.png";

        this.role = role != null ? role : UserRole.USER;
    }

    // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    // 비밀번호 암호화
    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    // 비밀번호 변경
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 닉네임 변경
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    // 프로필 이미지 변경
    public void changeProfileImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    // Refresh Token 저장/삭제
    public void updateRefreshToken(String token) {
        this.refreshToken = token;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }
}