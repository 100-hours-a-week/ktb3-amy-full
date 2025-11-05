package com.example.community.repository;

public interface UserRepositoryCustom {
    // 닉네임 부분일치로 사용자 수 카운트
    long countUsersByNicknameContains(String keyword);
}
