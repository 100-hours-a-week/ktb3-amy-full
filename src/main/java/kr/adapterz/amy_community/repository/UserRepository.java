package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.entity.User;
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
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 단건 조회 (로그인, JWT 인증 시 반드시 사용)
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByNickname(String nickname);

    // 닉네임 검색 (정렬: id desc)
    @Query("""
           select u
           from User u
           where lower(u.nickname) like lower(concat('%', :keyword, '%'))
           order by u.id desc
           """)
    List<User> searchByNickname(String keyword);

    // 닉네임으로 이메일 목록 조회
    @Query("select u.email from User u where u.nickname = :nickname")
    List<String> findEmailsByNickname(String nickname);

    // N+1 방지 (User + posts 즉시 로딩)
    @EntityGraph(attributePaths = "posts")
    List<User> findAllBy();

    // 리스트 기반 검색
    List<User> findByNicknameContainingIgnoreCase(String keyword);

    // Page 기반 검색
    Page<User> findByNicknameContainingIgnoreCase(String keyword, Pageable pageable);

    // Slice 기반 검색
    Slice<User> findSliceByNicknameContainingIgnoreCase(String keyword, Pageable pageable);
}