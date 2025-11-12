package com.example.community.repository;

import com.example.community.dto.UserInfoDto;
import com.example.community.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
    // 닉네임이 정확히 일치하는 사용자들을 id 내림차순으로 조회
    // List<UserEntity> findByNicknameContainingIgnoreCaseOrderByIdDesc(String keyword);

    @Query("""
           select u
           from UserEntity u
           where lower(u.nickname) like lower(concat('%', :keyword, '%'))
           order by u.id desc
           """)
    List<UserEntity> searchByNickname(String keyword);

    // 값(스칼라) 투영: 이메일만 뽑기
    @Query("select u.email from UserEntity u where u.nickname = :nickname")
    List<String> findEmailsByNickname(String nickname);

    // DTO 투영: id, email, nickname만 묶어서 반환
    @Query("""
           select new com.example.community.dto.UserInfoDto(u.id, u.email, u.nickname)
           from UserEntity u
           where lower(u.nickname) like lower(concat('%', :keyword, '%'))
           order by u.id desc
           """)
    List<UserInfoDto> findUserByNicknameWithDto(String keyword);

    // 특정 이메일이 존재하는지 확인 (true/false 반환)
    boolean existsByEmail(String email);

    // 닉네임이 일치하는 사용자 수를 카운트
    long countByNickname(String nickname);

    // 컬렉션(posts) 즉시 로딩: N+1 줄이기(중복 row 주의)
    @EntityGraph(attributePaths = "posts")
    List<UserEntity> findAllBy();


    // List: 닉네임 부분 검색
    List<UserEntity> findByNicknameContainingIgnoreCase(String keyword);

    // Page: 닉네임 부분 검색 + 페이징/정렬 + total count 포함
    Page<UserEntity> findByNicknameContainingIgnoreCase(String keyword, Pageable pageable);

    // Slice: 닉네임 부분 검색 + 다음 페이지 여부만
    Slice<UserEntity> findSliceByNicknameContainingIgnoreCase(String keyword, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);
}