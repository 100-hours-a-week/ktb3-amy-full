package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 중복 체크
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    // 조회
    UserEntity findByEmail(String email);
    UserEntity findByNickname(String nickname);
}
