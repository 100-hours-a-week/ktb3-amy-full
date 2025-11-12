package com.example.community.controller;

import com.example.community.dto.UserInfoDto;
import com.example.community.entity.UserEntity;
import com.example.community.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import com.example.community.dto.CreateUserRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            String token = userService.login(email, password);
            return Map.of(
                    "message", "login_success",
                    "data", Map.of("token", token)
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "message", e.getMessage(),
                    "data", null
            );
        }
    }

    @PostMapping
    public UserResponse create(@RequestBody CreateUserRequest request) {
        UserEntity saved = userService.create(request.getEmail(), request.getPassword(), request.getNickname(), request.getProfile_image());
        return UserResponse.of(saved);
    }

    @GetMapping(("/{id}"))
    public UserResponse findById(@PathVariable Long id) {
        return UserResponse.of(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        UserEntity updatedUser = userService.update(id, request.nickname);
        return UserResponse.of(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/search/nickname/keyword")
    public List<UserResponse> findByNicknameKeyword(@RequestParam String keyword) {
        return userService.findByNicknameKeyword(keyword).stream().map(UserResponse::of).toList();
    }

    @GetMapping("/emails/nickname")
    public List<String> findEmailsByNickname(@RequestParam String nickname) {
        return userService.findEmailsByNickname(nickname);
    }

    @GetMapping("/search/nickname")
    public List<UserInfoDto> findUserByNicknameWithDto(@RequestParam String keyword) {
        return userService.findUserByNicknameWithDto(keyword);
    }

    @GetMapping("/exists/email")
    public boolean existsByEmail(@RequestParam String email) {
        return userService.existsByEmail(email);
    }

    @GetMapping("/count/nickname")
    public long countByNickname(@RequestParam String nickname) {
        return userService.countByNickname(nickname);
    }

    @GetMapping("/with-posts/n-plus-one")
    public List<UserResponse> withPosts() {
        return userService.findAllUsersWithNPlusOne().stream()
                .peek(user -> {
                    // 강제 초기화
                    user.getPosts().size();
                })
                .map(UserResponse::of)
                .toList();
    }

    @GetMapping("/with-posts/entity-graph")
    public List<UserResponse> withPostsByEntityGraph() {
        return userService.findAllUsersWithEntityGraph().stream()
                .peek(user -> {
                    // 강제 초기화
                    user.getPosts().size();
                })
                .map(UserResponse::of)
                .toList();
    }

    // Pageable
    @GetMapping("/search/list")
    public List<UserResponse> searchList(@RequestParam String keyword) {
        return userService.searchAsList(keyword).stream()
                .map(UserResponse::of)
                .toList();
    }

    @GetMapping("/search/page")
    public Page<UserResponse> searchPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return userService.searchAsPage(keyword, page, size, sortBy, direction)
                .map(UserResponse::of); // Page<T>.map(...)
    }

    @GetMapping("/search/slice")
    public Slice<UserResponse> searchSlice(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return userService.searchAsSlice(keyword, page, size, sortBy, direction)
                .map(UserResponse::of); // Slice<T>.map(...)
    }

    // Custom Repository
    @GetMapping("/count/nickname/custom")
    public long countUsersByNicknameContains(@RequestParam String keyword) {
        return userService.countUsersByNicknameContains(keyword);
    }

    @Data
    public static class UpdateUserRequest {
        private String nickname;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String email;
        private String nickname;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public static UserResponse of(UserEntity userEntity) {
            return new UserResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getNickname(),
                    userEntity.getCreatedAt(),
                    userEntity.getUpdatedAt(),
                    userEntity.getCreatedBy(),
                    userEntity.getUpdatedBy());
        }

        public UserResponse(Long id, String email, String nickname, LocalDateTime createdAt, LocalDateTime updatedAt,
                            String createdBy, String updatedBy) {
            this.id = id;
            this.email = email;
            this.nickname = nickname;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.createdBy = createdBy;
            this.updatedBy = updatedBy;

        }
    }
}