package com.example.community.service;

import com.example.community.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PostService {

    //Repository 의존성 주입
    public PostRepository postRepository;
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    //게시글 작성
    public ResponseEntity<Map<String, Object>>createPost(Map<String, String> request) {
        //데이터에서 제목, 본문, 이미지 추출!
        String title = request.get("title");
        String content = request.get("content");
        String imageUrl = request.get("image_url");

        //필수 값 검증! (title, content)
        if (title == null || content == null) {
            //400
            return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
        }

        //게시글 데이터 구성
        Map<String, Object> postData = new HashMap<>();
        postData.put("title", title);
        postData.put("content", content);
        postData.put("image_url", imageUrl);
        postData.put("author", "amy");
        postData.put("date", new Date());
        postData.put("likes", 0);

        //Repository에 저장
        Map<String, Object> saved = postRepository.save(postData);

        //응답 데이터 구성
        Map<String, Object> response = Map.of("post_id", saved.get("post_id"));

        //201
        return ResponseEntity.status(201).body(Map.of("message", "post_created", "data", response));
    }

    //게시글 전체 목록 조회
    public ResponseEntity<Map<String, Object>> AllPosts() {
        return ResponseEntity.ok(Map.of("message", "post_list", "data", postRepository.findAll()));
    }

    //게시글 상세 조회
    public ResponseEntity<Map<String, Object>> PostDetail(Long postId) {
        //ID로 게시글 조회
        Map<String, Object> post = postRepository.findById(postId);

        //404
        if (post == null) {
            return ResponseEntity.status(404).body(Map.of("message", "post_not_found", "data", null));
        }

        //200
        return ResponseEntity.ok(Map.of("message", "post_detail", "data", post));
    }

    // 게시글 수정
    public ResponseEntity<Map<String, Object>> updatePost(Long postId, Map<String, String> request) {
        //게시글 존재 확인
        Map<String, Object> post = postRepository.findById(postId);
        if (post == null) {
            return ResponseEntity.status(404).body(Map.of("message", "post_not_found", "data", null));
        }

        //값 수정
        if (request.get("title") != null) post.put("title", request.get("title"));
        if (request.get("content") != null) post.put("content", request.get("content"));
        if (request.get("image_url") != null) post.put("image_url", request.get("image_url"));

        //수정된 게시글 저장
        postRepository.update(postId, post);

        //수정 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "post_updated", "data", Map.of("post_id", postId)));
    }

    //게시글 삭제
    public ResponseEntity<Map<String, Object>> deletePost(Long postId) {
        //404
        if (!postRepository.existById(postId)) {
            return ResponseEntity.status(404).body(Map.of("message", "post_not_found", "data", null));
        }

        //존재하면 삭제
        postRepository.delete(postId);

        //삭제 성공 메시지 반환
        return ResponseEntity.ok(Map.of("message", "post_deleted", "data", null));
    }
}