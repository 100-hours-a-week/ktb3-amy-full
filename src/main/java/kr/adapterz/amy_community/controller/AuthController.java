package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.dto.LoginRequest;
import kr.adapterz.amy_community.dto.SignupRequest;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest request) {

        User user = authService.signup(request);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "signup_success",
                        "data", Map.of(
                                "id", user.getId(),
                                "email", user.getEmail(),
                                "nickname", user.getNickname(),
                                "profileImageUrl", convertToAbsoluteUrl(user.getProfileImageUrl())
                        )
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {

        Map<String, String> tokens =
                authService.login(request.getEmail(), request.getPassword());

        User user = authService.getUserByEmail(request.getEmail());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "login_success",
                        "data", Map.of(
                                "accessToken", tokens.get("accessToken"),
                                "refreshToken", tokens.get("refreshToken"),
                                "userId", user.getId(),
                                "email", user.getEmail(),
                                "nickname", user.getNickname(),
                                "profileImageUrl", convertToAbsoluteUrl(user.getProfileImageUrl())
                        )
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestParam String refreshToken) {

        String newAccessToken = authService.reissueAccessToken(refreshToken);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "token_reissued",
                        "data", Map.of(
                                "accessToken", newAccessToken
                        )
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestParam Long userId) {

        authService.logout(userId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "logout_success"
                )
        );
    }

    private String convertToAbsoluteUrl(String url) {
        if (url != null && url.startsWith("/uploads"))
            return "http://localhost:8080" + url;
        return url;
    }
}