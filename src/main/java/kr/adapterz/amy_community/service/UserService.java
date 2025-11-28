package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.dto.SignupRequest;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.entity.UserRole;
import kr.adapterz.amy_community.repository.UserRepository;
import kr.adapterz.amy_community.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUtil fileUtil;

    // 외부 저장 경로 (프로필 이미지)
    private static final String PROFILE_DIR = "/Users/sumin/amy-community/uploads/profile/";

    @Transactional
    public User signup(SignupRequest request) {

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("duplicate_email");

        // 이메일 형식 검사
        if (!request.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))
            throw new IllegalArgumentException("invalid_email_format");

        // 비밀번호 형식 검사
        if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=<>?]).{8,20}$"))
            throw new IllegalArgumentException("invalid_password_format");

        // 비밀번호 재확인
        if (!request.getPassword().equals(request.getPasswordCheck()))
            throw new IllegalArgumentException("password_not_match");

        // 닉네임 검사
        if (request.getNickname().trim().isEmpty())
            throw new IllegalArgumentException("nickname_required");
        if (request.getNickname().contains(" "))
            throw new IllegalArgumentException("nickname_no_space");
        if (request.getNickname().length() > 10)
            throw new IllegalArgumentException("nickname_length");

        if (userRepository.countByNickname(request.getNickname()) > 0)
            throw new IllegalArgumentException("duplicate_nickname");

        // 프로필 이미지 저장
        String fileName = "default-profile.png";

        if (request.getProfileImageBase64() != null &&
                !request.getProfileImageBase64().isBlank()) {

            String base64 = request.getProfileImageBase64();

            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }

            fileName = System.currentTimeMillis() + ".png";

            try {
                fileUtil.saveBase64Image(base64, PROFILE_DIR, fileName);
            } catch (Exception e) {
                throw new IllegalArgumentException("image_save_fail");
            }
        }

        String profileUrl = "/uploads/profile/" + fileName;

        // 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPw)
                .nickname(request.getNickname())
                .profileImageUrl(profileUrl)
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("unauthorized"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new IllegalArgumentException("unauthorized");

        return user;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    @Transactional
    public User update(Long id, String nickname, Boolean deleteImage, String profileImageBase64) {

        User user = findById(id);

        // 닉네임 변경
        if (nickname != null && !nickname.isBlank()) {

            if (nickname.contains(" "))
                throw new IllegalArgumentException("nickname_no_space");

            if (nickname.length() > 10)
                throw new IllegalArgumentException("nickname_length");

            if (!nickname.equals(user.getNickname()) &&
                    userRepository.countByNickname(nickname) > 0)
                throw new IllegalArgumentException("duplicate_nickname");

            user.changeNickname(nickname);
        }

        // 프로필 이미지 삭제
        if (Boolean.TRUE.equals(deleteImage)) {
            user.changeProfileImage("/uploads/profile/default-profile.png");
        }

        // 새 프로필 업로드
        else if (profileImageBase64 != null && !profileImageBase64.isBlank()) {

            String base64 = profileImageBase64;
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }

            String newFile = System.currentTimeMillis() + ".png";

            try {
                fileUtil.saveBase64Image(base64, PROFILE_DIR, newFile);
            } catch (Exception e) {
                throw new IllegalArgumentException("image_save_fail");
            }

            user.changeProfileImage("/uploads/profile/" + newFile);
        }

        return user;
    }

    @Transactional
    public User updatePassword(Long id, String newPassword, String check) {

        User user = findById(id);

        if (!newPassword.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=<>?]).{8,20}$"))
            throw new IllegalArgumentException("invalid_password_format");

        if (!newPassword.equals(check))
            throw new IllegalArgumentException("password_not_match");

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);

        return user;
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public long countByNickname(String nickname) {
        return userRepository.countByNickname(nickname);
    }
}