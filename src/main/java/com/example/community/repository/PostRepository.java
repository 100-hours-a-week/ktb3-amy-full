package com.example.community.repository;

import com.example.community.dto.PostSummaryDto;
import com.example.community.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query("""
           select p
           from PostEntity p
           where lower(p.title) like lower(concat('%', :keyword, '%'))
           order by p.id desc
           """)
    List<PostEntity> searchByTitle(String keyword);

    @Query("""
           select p
           from PostEntity p
           where p.author.nickname = :nickname
           order by p.id desc
           """)
    List<PostEntity> findByAuthorNickname(String nickname);

    @Query("select p.title from PostEntity p where p.author.id = :authorId order by p.id desc")
    List<String> findTitlesByAuthorId(Long authorId);

    @Query("""
           select new com.example.community.dto.PostSummaryDto(
                    p.id, p.title, p.author.nickname)
           from PostEntity p
           where lower(p.title) like lower(concat('%', :keyword, '%'))
           order by p.id desc
           """)
    List<PostSummaryDto> findPostSummaries(String keyword);

    // author 즉시 로딩 + 페이징
    @EntityGraph(attributePaths = {"author"})
    @Query(value = "select p from PostEntity p order by p.id desc",
            countQuery = "select count(p) from PostEntity p")
    Page<PostEntity> findAllWithAuthor(Pageable pageable);

    // author 즉시 로딩 (페이징 없이 전체)
    @EntityGraph(attributePaths = {"author"})
    @Query("select p from PostEntity p order by p.id desc")
    List<PostEntity> findAllWithAuthor();

    List<PostEntity> findByTitleContainingIgnoreCase(String keyword);
    Page<PostEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Slice<PostEntity> findSliceByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Slice<PostEntity> findAllBy(Pageable pageable);
}