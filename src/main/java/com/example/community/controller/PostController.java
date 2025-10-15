package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {
    public Map<Long, Map<String, Object>> postStore = new HashMap<>();
    public long postId = 1;

    //게시글 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, String> request) {
         String title = request.get("title");
         String content = request.get("content");
         String imageUrl = request.get("image_url");

         //400
         if(title == null || content == null) {
             return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
         }

         Map<String, Object> postData = new HashMap<>();
         postData.put("post_id", postId);
         postData.put("title", title);
         postData.put("content", content);
         postData.put("image_url", imageUrl);
         postData.put("author", "amy");
         postData.put("date", new Date());
         postStore.put(postId, postData);

         //201
         Map<String, Object> responseData = Map.of("post_id", postId++);
         return ResponseEntity.status(201).body(Map.of("message", "post_created", "data", responseData));
    }

    //게시글 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> ALL() {
        return ResponseEntity.ok(Map.of("message", "post_list", "data", postStore.values()));
    }

    //게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> Detail(@PathVariable Long postId) {
        Map<String, Object> post = postStore.get(postId);

        //404
        if (post == null) {
            return ResponseEntity.status(404).body(Map.of("message", "post_not_found", "data", null));
        }

        return ResponseEntity.ok(Map.of("message", "post_detail", "data", post));
    }
}