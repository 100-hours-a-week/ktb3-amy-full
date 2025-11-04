package com.example.community.service;

import com.example.community.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LikeService {

    public PostRepository postRepository;

    // 생성자 주입
    public LikeService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 좋아요 추가
    public ResponseEntity<Map<String, Object>> addLike(Long postId) {
        // 게시글 조회
        Map<String, Object> post = postRepository.findById(postId);

        // 404
        if (post == null) {
            return ResponseEntity.status(404).body(Map.of("message", "post_not_found", "data", null));
        }

        // 좋아요 수 증가
        int like = (int) post.getOrDefault("like", 0);
        post.put("like", like + 1);

        //수정된 데이터 저장
        postRepository.update(postId,post);

        // 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "like_added", "data", Map.of("like", post.get("like"))));
    }

    // 좋아요 취소
    public ResponseEntity<Map<String, Object>> removeLike(Long postId) {
        // 게시글 조회
        Map<String, Object> post = postRepository.findById(postId);

        //404
        if (post == null) {
            return ResponseEntity.status(404).body(Map.of("message", "post_not_found", "data", null));
        }

        // 좋아요 수 갑소 (0 이하로 내려가지 않게 처리)
        int like = (int) post.getOrDefault("like", 0);
        post.put("like", Math.max(like - 1, 0));

        // 수정된 데이터 저장
        postRepository.update(postId, post);

        // 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "like_removed", "data", Map.of("like", post.get("like"))));
    }
}