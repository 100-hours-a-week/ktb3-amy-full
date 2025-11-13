package com.example.community.service;

import com.example.community.dto.PostSummaryDto;
import com.example.community.entity.PostEntity;
import com.example.community.entity.UserEntity;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostEntity create(Long authorId, String title, String content) {
        if (authorId == null) {
            throw new IllegalArgumentException("authorId must not be null");
        }

        UserEntity author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("userEntity not found"));

        PostEntity postEntity = new PostEntity(title, content, author);

        // 저장
        PostEntity saved = postRepository.save(postEntity);

        // 즉시 flush() 실행 — DB에 insert 쿼리 반영
        postRepository.flush();

        // DB에서 다시 조회 — Auditing 필드가 채워진 최신 상태 반환
        return postRepository.findById(saved.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("postEntity not found"));
    }

    public PostEntity findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("postEntity not found"));
    }

    @Transactional
    public PostEntity update(Long id, String title, String content) {
        PostEntity postEntity = findById(id);
        if (title != null) postEntity.changeTitle(title);
        if (content != null) postEntity.changeContent(content);
        return postEntity;
    }

    @Transactional
    public void delete(Long id) {
        postRepository.delete(findById(id));
    }

    public List<PostEntity> findByTitle(String keyword) {
        // return postRepository.findByTitleContainingIgnoreCase(keyword);
        return postRepository.searchByTitle(keyword);
    }

    public List<PostEntity> findByAuthorNickname(String nickname) {
        // return postRepository.findByAuthor_Nickname(nickname);
        return postRepository.findByAuthorNickname(nickname);
    }

    public List<String> findTitlesByAuthorId(Long authorId) {
        return postRepository.findTitlesByAuthorId(authorId);
    }

    public List<PostSummaryDto> findPostSummaries(String keyword) {
        return postRepository.findPostSummaries(keyword);
    }

    public List<PostEntity> findALlPostsWithNPlusOne() {
        return postRepository.findAll();
    }

    public List<PostEntity> findAllPostsByEntityGraph() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "postId"));
        return postRepository.findAllWithAuthor(pageable).getContent();
    }

    public List<PostEntity> findAllPosts(int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "postId"));
        return postRepository.findAllWithAuthor(pageable).getContent();
    }

    // pageable
    // List
    public List<PostEntity> searchAsList(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Page
    public Page<PostEntity> searchAsPage(String keyword, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    // Slice
    public Slice<PostEntity> searchAsSlice(String keyword, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (keyword == null || keyword.isBlank()) {
            return postRepository.findAllBy(pageable);
        }
        return postRepository.findSliceByTitleContainingIgnoreCase(keyword, pageable);
    }
}