package kr.adapterz.amy_community.controller;

import jakarta.validation.Valid;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/me")
    public ResponseEntity<?> me(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                Map.of(
                        "message", "me_success",
                        "data", UserResponse.of(user)
                )
        );
    }

    @GetMapping("/users/exists/email")
    public boolean existsByEmail(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/users/exists/nickname")
    public boolean existsByNickname(@RequestParam String nickname) {
        return userService.countByNickname(nickname) > 0;
    }

    @GetMapping("/users/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return UserResponse.of(userService.findById(id));
    }

    @PatchMapping("/users/me")
    public UserResponse update(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User loginUser = (User) authentication.getPrincipal();

        User updated = userService.update(
                loginUser.getId(),     // ID를 직접 받지 않음 (보안 강화)
                request.nickname,
                request.deleteImage,
                request.profileImageBase64
        );

        return UserResponse.of(updated);
    }

    @PatchMapping("/users/me/password")
    public UserResponse updatePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordUpdateRequest request
    ) {
        User loginUser = (User) authentication.getPrincipal();

        User updated = userService.updatePassword(
                loginUser.getId(),
                request.newPassword,
                request.newPasswordCheck
        );

        return UserResponse.of(updated);
    }

    @DeleteMapping("/users/me")
    public void delete(Authentication authentication) {
        User loginUser = (User) authentication.getPrincipal();
        userService.delete(loginUser.getId());
    }

    @Data
    public static class UpdateUserRequest {
        public String nickname;
        public Boolean deleteImage;
        public String profileImageBase64;
    }

    @Data
    public static class PasswordUpdateRequest {
        public String newPassword;
        public String newPasswordCheck;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String email;
        private String nickname;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public static UserResponse of(User user) {

            String profileUrl = user.getProfileImageUrl();
            if (profileUrl != null && profileUrl.startsWith("/uploads")) {
                profileUrl = "http://localhost:8080" + profileUrl;
            }

            return new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    profileUrl,
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getCreatedBy(),
                    user.getUpdatedBy()
            );
        }

        public UserResponse(Long id, String email, String nickname,
                            String profileImageUrl,
                            LocalDateTime createdAt, LocalDateTime updatedAt,
                            String createdBy, String updatedBy) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.createdBy = createdBy;
            this.updatedBy = updatedBy;
        }
    }
}