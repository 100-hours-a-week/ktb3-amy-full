package com.example.community.service;

import com.example.community.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    // Repository 주입
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //회원가입
    public ResponseEntity<Map<String, Object>> signup(Map<String, String> request) {

        //값 추출
        String email = request.get("email");
        String password = request.get("password");
        String nickname = request.get("nickname");

        //필수 입력값(email, password, nickname) 검증
        if (email == null || password == null || nickname == null) {
            //400
            return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
        }

        //회원 데이터 생성
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("nickname", nickname);

        //Repository에 저장 후 ID 반환
        Map<String, Object> data = userRepository.save(userData);

        //201
        return ResponseEntity.status(201).body(Map.of("message", "register_success", "data", data));
    }

    //회원 정보 수정
    public ResponseEntity<Map<String, Object>> updateUser(Long userId, Map<String, String> request) {

        // 해당 ID의 회원 정보 조회
        Map<String, String> user = userRepository.findById(userId);

        //404
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "user_not_found", "data", null));
        }

        //기존 회원 데이터를 복사하여 수정할 데이터 생성
        Map<String, String> updatedUser = new HashMap<>(user);

        //닉네임 및 프로필 이미지 변경 (입력값 있을 경우에만!)
        if (request.get("nickname") != null) updatedUser.put("nickname", request.get("nickname"));
        if (request.get("profile_image") != null) updatedUser.put("profile_image", request.get("profile_image"));

        //수정된 회원 정보 저장
        userRepository.update(userId, updatedUser);

        //수정 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "user_updated", "data", Map.of("user_id", userId, "nickname", updatedUser.get("nickname"))
        ));
    }

    //비밀번호 수정
    public ResponseEntity<Map<String, Object>> updatePassword(Long userId, Map<String, String> request) {

        //ID로 회원 정보 조회
        Map<String, String> user = userRepository.findById(userId);

        //404
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "user_not_found", "data", null));
        }

        //비밀번호 및 비밀번호 확인 값 추출
        String pw = request.get("password");
        String confirm = request.get("password_confirm");

        //비밀번호 검증 (값이 없거나 일치하지 않으면 실패!)
        if (pw == null || confirm == null || !pw.equals(confirm)) {
            return ResponseEntity.badRequest().body(Map.of("message", "password_mismatch", "data", null));
        }

        //기존 사용자 정보 복사 후 비밀번호 변경
        Map<String, String> updatedUser = new HashMap<>(user);
        updatedUser.put("password", pw);

        //변경된 정보 저장
        userRepository.update(userId, updatedUser);

        //성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "password_updated", "data", null));
    }

    //회원 탈퇴
    public ResponseEntity<Map<String, Object>> deleteUser(Long userId) {

        //회원 존재 여부 확인
        if (!userRepository.existById(userId)) {
            //404
            return ResponseEntity.status(404).body(Map.of("message", "user_not_found", "data", null));
        }

        //존재 시 회원 정보 삭제
        userRepository.delete(userId);

        //삭제 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "user_deleted", "data", null));
    }
}
