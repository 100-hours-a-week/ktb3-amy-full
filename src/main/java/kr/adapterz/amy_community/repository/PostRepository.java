package kr.adapterz.amy_community.repository;

import kr.adapterz.amy_community.dto.PostSummaryDto;
import kr.adapterz.amy_community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 제목 검색 (부분 일치)
    @Query("""
           select p
           from Post p
           where lower(p.title) like lower(concat('%', :keyword, '%'))
           order by p.id desc
           """)
    List<Post> searchByTitle(String keyword);

    // 작성자 닉네임으로 게시글 검색
    @Query("""
           select p
           from Post p
           where p.author.nickname = :nickname
           order by p.id desc
           """)
    List<Post> findByAuthorNickname(String nickname);

    // 특정 사용자 게시글 제목만 추출
    @Query("""
           select p.title
           from Post p
           where p.author.id = :authorId
           order by p.id desc
           """)
    List<String> findTitlesByAuthorId(Long authorId);

    // DTO Projection
    @Query("""
           select new kr.adapterz.amy_community.dto.PostSummaryDto(
                   p.id,
                   p.title,
                   p.author.nickname
           )
           from Post p
           where lower(p.title) like lower(concat('%', :keyword, '%'))
           order by p.id desc
           """)
    List<PostSummaryDto> findPostSummaries(String keyword);

    // author 즉시 로딩(N+1 방지)
    @EntityGraph(attributePaths = "author")
    List<Post> findAllBy();

    // 기본 검색 (정렬 없음)
    List<Post> findByTitleContainingIgnoreCase(String keyword);

    // Page 검색 (total count 포함)
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    // Slice 검색 (다음 페이지 여부만)
    Slice<Post> findSliceByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}