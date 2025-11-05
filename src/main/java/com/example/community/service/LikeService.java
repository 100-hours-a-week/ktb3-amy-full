package com.example.community.service;

import com.example.community.entity.PostEntity;
import com.example.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final PostRepository postRepository;

    // 좋아요 추가
    @Transactional
    public ResponseEntity<Map<String, Object>> addLike(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElse(null);

        if (post == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "post_not_found", "data", null));
        }

        // 좋아요 수 +1
        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);

        return ResponseEntity.ok(Map.of(
                "message", "like_added",
                "data", Map.of("like", post.getLikes())
        ));
    }

    // 좋아요 취소
    @Transactional
    public ResponseEntity<Map<String, Object>> removeLike(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElse(null);

        if (post == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "post_not_found", "data", null));
        }

        // 좋아요 수 감소 (0 이하로 내려가지 않게 처리)
        post.setLikes(Math.max(post.getLikes() - 1, 0));
        postRepository.save(post);

        return ResponseEntity.ok(Map.of(
                "message", "like_removed",
                "data", Map.of("like", post.getLikes())
        ));
    }
}