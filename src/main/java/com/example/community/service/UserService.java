package com.example.community.service;

import com.example.community.dto.UserInfoDto;
import com.example.community.entity.UserEntity;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    public boolean login(String email, String password) {
        // 이메일로 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("unauthorized"));

        // 비밀번호 비교
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("unauthorized");
        }

        // 로그인 성공
        return true;
    }

    @Transactional
    public UserEntity create(String email, String password, String nickname, String profile_image) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("duplicate_email");
        }
        UserEntity userEntity = new UserEntity(email, password, nickname, profile_image);
        return userRepository.save(userEntity);
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("userEntity not found"));
    }

    public UserEntity getReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }

    @Transactional
    public UserEntity update(Long id, String nickname) {
        UserEntity userEntity = findById(id);
        if (nickname != null) {
            userEntity.changeNickname(nickname);
        }
        return userEntity;
    }

    @Transactional
    public void delete(Long id) {
        // 프록시 반환 (접근 시 초기화)
        userRepository.delete(findById(id));
    }

    public List<UserEntity> findByNicknameKeyword(String keyword) {
        // return userRepository.findByNicknameContainingIgnoreCaseOrderByIdDesc(keyword);
        return userRepository.searchByNickname(keyword);
    }

    public List<String> findEmailsByNickname(String nickname) {
        return userRepository.findEmailsByNickname(nickname);
    }

    public List<UserInfoDto> findUserByNicknameWithDto(String keyword) {
        return userRepository.findUserByNicknameWithDto(keyword);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public long countByNickname(String nickname) {
        return userRepository.countByNickname(nickname);
    }

    // author는 프록시. 컨트롤러에서 닉네임 접근할 때마다 추가 SELECT → N+1
    public List<UserEntity> findAllUsersWithNPlusOne() {
        return userRepository.findAll();
    }

    // posts 즉시 로딩(중복 row→List엔 중복 요소 X, 하지만 SQL은 join됨)
    public List<UserEntity> findAllUsersWithEntityGraph() {
        return userRepository.findAllBy();
    }

    // Pageable
    // List
    public List<UserEntity> searchAsList(String keyword) {
        return userRepository.findByNicknameContainingIgnoreCase(keyword);
    }

    // Page
    public Page<UserEntity> searchAsPage(String keyword, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByNicknameContainingIgnoreCase(keyword, pageable);
    }

    // Slice
    public Slice<UserEntity> searchAsSlice(String keyword, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findSliceByNicknameContainingIgnoreCase(keyword, pageable);
    }

    // Custom Repository
    public long countUsersByNicknameContains(String keyword) {
        return userRepository.countUsersByNicknameContains(keyword);
    }
}