package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.dto.user.UserLoginRequest;
import kr.adapterz.amy_community.dto.user.UserResponse;
import kr.adapterz.amy_community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // 회원가입 (multipart/form-data)
    @PostMapping(consumes = "multipart/form-data")
    public Map<String, Object> signup(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("nickname") String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        UserResponse data = userService.signup(email, password, nickname, profileImage);
        return Map.of("message", "signup_success", "data", data);
    }

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserLoginRequest req) {
        UserResponse data = userService.login(req);

        return (data == null)
                ? Map.of("message", "login_fail")
                : Map.of("message", "login_success", "data", data);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        UserResponse data = userService.getUser(id);

        return (data == null)
                ? Map.of("message", "user_not_found")
                : Map.of("message", "user_detail", "data", data);
    }

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public Map<String, Object> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return Map.of("exists", exists);
    }

    // 회원 정보 수정 (닉네임 + 프로필 이미지)
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public Map<String, Object> updateUser(
            @PathVariable Long id,
            @RequestParam("nickname") String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        UserResponse data = userService.updateUser(id, nickname, profileImage);
        return Map.of("message", "user_updated", "data", data);
    }

    // ⭐⭐ 비밀번호 변경 API (추가된 부분) ⭐⭐
    @PutMapping("/{id}/password")
    public Map<String, Object> updatePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> req
    ) {
        String password = req.get("password");

        boolean ok = userService.updatePassword(id, password);

        return Map.of(
                "message", ok ? "password_updated" : "user_not_found"
        );
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = userService.deleteUser(id);
        return Map.of("message", ok ? "user_deleted" : "user_not_found");
    }
}
