package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    PostLikeEntity findByPostIdAndUserId(Long postId, Long userId);

}