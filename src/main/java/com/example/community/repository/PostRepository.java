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
    // 제목에 키워드 포함(대소문자 무시)
    // List<PostEntity> findByTitleContainingIgnoreCase(String keyword);

    @Query("""
           select p
           from PostEntity p
           where lower(p.title) like lower(concat('%', :keyword, '%'))
           order by p.id desc
           """)
    List<PostEntity> searchByTitle(String keyword);

    // 닉네임이 정확히 일치하는 작성자의 글
    // List<PostEntity> findByAuthor_Nickname(String nickname);

    @Query("""
           select p
           from PostEntity p
           where p.author.nickname = :nickname
           order by p.id desc
           """)
    List<PostEntity> findByAuthorNickname(String nickname);

    // 특정 작성자의 글 제목만
    @Query("select p.title from PostEntity p where p.author.id = :authorId order by p.id desc")
    List<String> findTitlesByAuthorId(Long authorId);

    // DTO 사용: 게시글 요약(id, title, authorNickname)
    @Query("""
           select new com.example.community.dto.PostSummaryDto(
                    p.id, p.title, p.author.nickname)
           from PostEntity p
           where lower(p.title) like lower(concat('%', :keyword, '%'))
           order by p.id desc
           """)
    List<PostSummaryDto> findPostSummaries(String keyword);

    // EntityGraph로 author 즉시 로딩하여 N+1 방지
    @EntityGraph(attributePaths = "author") // author만 즉시 로딩
    List<PostEntity> findAllBy();

    // 제목에 키워드가 포함된 게시글들 검색 (대소문자 무시, 전체 결과 반환)
    List<PostEntity> findByTitleContainingIgnoreCase(String keyword);

    // 제목에 키워드가 포함된 게시글들 검색 (대소문자 무시, 페이징/정렬 포함 + 전체 건수까지 조회)
    Page<PostEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // 제목에 키워드가 포함된 게시글들 검색 (대소문자 무시, 페이징/정렬 포함 + 다음 페이지 존재 여부만 확인, total count 쿼리 X)
    Slice<PostEntity> findSliceByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}