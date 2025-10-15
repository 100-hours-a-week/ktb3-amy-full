package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    public Map<Long, Map<String, String>> userStore = new HashMap<>();
    public long userId = 1;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");
        String nickname = request.get("nickname");

        //400
        if (email == null || password == null || nickname == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
        }
        //409
        for (Map<String, String> user : userStore.values()) {
            if (user.get("email").equals(email)) {
                return ResponseEntity.status(409).body(Map.of("message", "duplicate_email", "data", null));
            }
        }
        //201
        userStore.put(userId, Map.of("email", email, "password", password, "nickname", nickname));
        Map<String, Object> data = Map.of("user_id", userId++);
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
        //200
        for (Map<String, String> user : userStore.values()) {
            if(user.get("email").equals(email) && user.get("password").equals(password)) {
                Map<String, Object> data = Map.of("token", "token" + email);
                return ResponseEntity.ok(Map.of("message", "login_success", "data", data));
            }
        }
        //401
        return ResponseEntity.status(401).body(Map.of("message", "unauthorized", "data", null));
    }
}