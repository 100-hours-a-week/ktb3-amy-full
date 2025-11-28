package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 유저가 특정 게시글에 좋아요 누른 상태인지 여부
    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    // 유저가 특정 게시글 좋아요 취소
    void deleteByUser_IdAndPost_Id(Long userId, Long postId);

    // 게시글의 좋아요 개수
    long countByPost_Id(Long postId);

    // 게시글 삭제 시 해당 게시글의 좋아요 모두 삭제
    @Modifying
    @Transactional
    @Query("delete from Like l where l.post.id = :postId")
    void deleteAllByPostId(Long postId);
}