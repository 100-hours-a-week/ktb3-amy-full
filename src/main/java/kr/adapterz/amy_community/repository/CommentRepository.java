package kr.adapterz.amy_community.repository;


import kr.adapterz.amy_community.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
