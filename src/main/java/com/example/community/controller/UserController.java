package com.example.community.controller;

import com.example.community.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    //UserService 의존성 주입
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //회원가입 API
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "register_success"),
            @ApiResponse(responseCode = "409", description = "duplicate_email")
    })

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        //요청을 UserService로 전달하고 결과 반환
        return userService.signup(request);
    }

    //회원 정보 수정 API
    @Operation(summary = "회원 정보 수정", description = "닉네임 또는 프로필 이미지를 변경합니다.")

    //회원 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        return userService.updateUser(userId, request);
    }

    //비밀번호 변경 API
    @Operation(summary = "비밀번호 수정", description = "새로운 비밀번호로 변경합니다.")

    //비밀번호 변경
    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        return userService.updatePassword(userId, request);
    }

    //회원 탈퇴 API
    @Operation(summary = "회원 탈퇴", description = "계정을 삭제합니다.")

    //회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}