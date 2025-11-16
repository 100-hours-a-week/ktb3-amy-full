package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
