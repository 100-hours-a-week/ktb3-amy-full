package com.example.community.repository;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepository {

    //DB 대신 HashMap 사용!
    public Map<Long, Map<String, String>> userStore = new HashMap<>();
    public long userId = 1;

    //회원 데이터 저장 (회원가입 시 사용)
    public Map<String, Object> save(Map<String, String> userData) {
        //새로운 회원 정보를 userStore에 추가 (key: userId, value: 회원 정보)
        userStore.put(userId, userData);

        //회원 ID 반환
        Map<String, Object> response = Map.of("user_id", userId++);

        //저장 결과 반환
        return response;
    }

    //전체 회원 목록 조회
    public Collection<Map<String, String>> findAll() {
        return userStore.values();
    }

    //회원 ID로 특정 회원 정보 조회
    public Map<String, String> findById(Long id) {
        return userStore.get(id);
    }

    //회원 정보 업데이트
    public void update(Long id, Map<String, String> updatedUser) {
        userStore.put(id, updatedUser);
    }

    //회원 삭제
    public void delete(Long id) {
        userStore.remove(id);
    }

    //회원 존재 여부 확인
    public boolean existById(Long id) {
        return userStore.containsKey(id);
    }
}