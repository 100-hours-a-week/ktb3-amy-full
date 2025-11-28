package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글의 모든 댓글 (최신순)
    List<Comment> findByPost_IdOrderByIdDesc(Long postId);

    // 게시글 삭제 시 댓글 전체 삭제
    @Modifying
    @Transactional
    @Query("delete from Comment c where c.post.id = :postId")
    void deleteAllByPostId(Long postId);
}