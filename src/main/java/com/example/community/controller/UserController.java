package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    //DB 대신 HashMap 사용
    //key: userId / value: 회원 정보 (Map<String, String>)
    public Map<Long, Map<String, String>> userStore = new HashMap<>();
    public long userId = 1; //회원 번호 자동 증가

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        String nickname = request.get("nickname");

        //400 (정보 누락)
        if (email == null || password == null || nickname == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
        }
        //409
        for (Map<String, String> user : userStore.values()) {
            if (user.get("email").equals(email)) {
                return ResponseEntity.status(409).body(Map.of("message", "duplicate_email", "data", null));
            }
        }

        //회원 데이터 저장
        userStore.put(userId, Map.of("email", email, "password", password, "nickname", nickname));
        Map<String, Object> data = Map.of("user_id", userId++);

        //201
        return ResponseEntity.status(201).body(Map.of("message", "register_success", "data", data));
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        //400
        if(email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
        }

        //200 (등록된 회원 찾기)
        for (Map<String, String> user : userStore.values()) {
            if(user.get("email").equals(email) && user.get("password").equals(password)) {
                Map<String, Object> data = Map.of("token", "token" + email);
                return ResponseEntity.ok(Map.of("message", "login_success", "data", data));
            }
        }

        //401 (실패)
        return ResponseEntity.status(401).body(Map.of("message", "unauthorized", "data", null));
    }

    //회원 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> request) {

        //404 (회원 존재 여부)
        Map<String, String> user = userStore.get(userId);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "user_not_found", "data", null));
        }

        //데이터 가져오기
        String nickname = request.get("nickname");
        String profileImage = request.get("profile_image");

        //기존 데이터 수정
        Map<String, String> updatedUser = new HashMap<>(user);
        if (nickname != null) updatedUser.put("nickname", nickname);
        if (profileImage != null) updatedUser.put("profile_image", profileImage);

        //데이터 업데이트
        userStore.put(userId, updatedUser);

        return ResponseEntity.ok(Map.of("message", "user_updated", "data", Map.of("user_id", userId, "nickname", updatedUser.get("nickname"))));
    }

    //비밀번호 수정
    @PutMapping("/{userId}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {

        Map<String, String> user = userStore.get(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "user_not_found", "data", null));
        }

        String pw = request.get("password");
        String confirm = request.get("password_confirm");

        //비밀번호 불일치
        if (pw == null || confirm == null || !pw.equals(confirm)) {
            return ResponseEntity.badRequest().body(Map.of("message", "password_mismatch", "data", null));
        }

        //비밀번호 변경 후 저장
        Map<String, String> updatedUser = new HashMap<>(user);
        updatedUser.put("password", pw);
        userStore.put(userId, updatedUser);

        return ResponseEntity.ok(Map.of("message", "password_updated", "data", null));
    }

    //회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        if (!userStore.containsKey(userId)) {
            return ResponseEntity.status(404).body(Map.of("message", "user_not_found", "data", null));
        }

        userStore.remove(userId);
        return ResponseEntity.ok(Map.of("message", "user_deleted", "data", null));
    }
}