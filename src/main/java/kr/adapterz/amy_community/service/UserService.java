package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.dto.user.UserLoginRequest;
import kr.adapterz.amy_community.dto.user.UserResponse;
import kr.adapterz.amy_community.entity.UserEntity;
import kr.adapterz.amy_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 이메일 중복 체크
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 체크
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원가입
    public UserResponse signup(String email, String password, String nickname, MultipartFile profileImage) {

        String img64 = null;
        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                img64 = Base64.getEncoder().encodeToString(profileImage.getBytes());
            }
        } catch (Exception ignored) {}

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setProfileImage(img64);

        userRepository.save(user);
        return new UserResponse(user);
    }

    // 로그인
    public UserResponse login(UserLoginRequest req) {
        UserEntity user = userRepository.findByEmail(req.getEmail());
        if (user != null && user.getPassword().equals(req.getPassword())) {
            return new UserResponse(user);
        }
        return null;
    }

    // 단건 조회
    public UserResponse getUser(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::new)
                .orElse(null);
    }

    // 회원 정보 수정 (닉네임 + 이미지)
    public UserResponse updateUser(Long id, String nickname, MultipartFile image) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) return null;

        user.setNickname(nickname);

        if (image != null && !image.isEmpty()) {
            try {
                String encoded = Base64.getEncoder().encodeToString(image.getBytes());
                user.setProfileImage(encoded);
            } catch (Exception ignored) {}
        }

        return new UserResponse(user);
    }

    // 비밀번호 변경
    public boolean updatePassword(Long id, String password) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) return false;

        user.setPassword(password);
        return true;
    }

    // 회원 탈퇴
    public boolean deleteUser(Long id) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) return false;

        userRepository.delete(user);
        return true;
    }
}
