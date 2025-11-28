package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.dto.SignupRequest;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.entity.UserRole;
import kr.adapterz.amy_community.jwt.TokenProvider;
import kr.adapterz.amy_community.repository.UserRepository;
import kr.adapterz.amy_community.util.FileUtil;
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
    private final FileUtil fileUtil;

    // 외부 저장 경로
    private static final String PROFILE_DIR = "/Users/sumin/amy-community/uploads/profile/";

    @Transactional
    public User signup(SignupRequest request) {

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("duplicate_email");
        }

        // 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(request.getPassword());

        // 기본 프로필
        String fileName = "default-profile.png";

        // Base64 존재하면 업로드 처리
        if (request.getProfileImageBase64() != null &&
                !request.getProfileImageBase64().isBlank()) {

            String base64 = request.getProfileImageBase64();

            // Base64 prefix 제거
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }

            // timestamp 파일명 생성
            fileName = System.currentTimeMillis() + ".png";

            try {
                fileUtil.saveBase64Image(base64, PROFILE_DIR, fileName);
            } catch (Exception e) {
                throw new IllegalArgumentException("image_save_fail");
            }
        }

        String profileUrl = "/uploads/profile/" + fileName;

        // User 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPw)
                .nickname(request.getNickname())
                .profileImageUrl(profileUrl)
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

        // Refresh Token 저장
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