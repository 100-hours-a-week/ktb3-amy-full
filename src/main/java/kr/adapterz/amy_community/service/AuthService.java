package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.dto.SignupRequest;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.entity.UserRole;
import kr.adapterz.amy_community.jwt.TokenProvider;
import kr.adapterz.amy_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public User signup(SignupRequest request) {

        // 이메일 중복
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("duplicate_email");
        }

        // 암호화 비밀번호
        String encodedPw = passwordEncoder.encode(request.getPassword());

        // User 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPw)
                .nickname(request.getNickname())
                .profileImageUrl("/uploads/profile/default.png")
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public Map<String, String> login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("invalid_email_or_password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("invalid_email_or_password");
        }

        // JWT 생성
        String accessToken = tokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        // RefreshToken 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {

        Long userId = tokenProvider.getUserId(refreshToken);

        // DB Refresh Token 비교
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("invalid_refresh_token");
        }

        return tokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    @Transactional
    public void logout(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));

        user.clearRefreshToken();
        userRepository.save(user);
    }
}