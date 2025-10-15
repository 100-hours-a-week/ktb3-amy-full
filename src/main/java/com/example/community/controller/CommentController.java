package com.example.community.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    public Map<Long, Map<String, Object>> commentStore = new HashMap<>();
    public long commentId = 1;

    //댓글 작성
    @PostMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> createComment(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "invalid_request", "data", null));
        }

        Map<String, Object> comment = new HashMap<>();
        comment.put("comment_id", commentId);
        comment.put("post_id", postId);
        comment.put("content", content);
        comment.put("author", "amy");
        comment.put("date", new Date());
        commentStore.put(commentId, comment);

        //201
        Map<String, Object> data = Map.of("comment_id", commentId++);
        return ResponseEntity.status(201).body(Map.of("message", "comment_created", "data", data));
    }

    //댓글 목록 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> getCommentsByPost(@PathVariable Long postId) {
        List<Map<String, Object>> comments = new ArrayList<>();
        for (Map<String, Object> comment : commentStore.values()) {
            if (Objects.equals(comment.get("post_id"), postId)) {
                comments.add(comment);
            }
        }
        return ResponseEntity.ok(Map.of("message", "comment_list", "data", comments));
    }

    //댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable Long commentId, @RequestBody Map<String, String> request) {
        Map<String, Object> comment = commentStore.get(commentId);
        if (comment == null) {
            return ResponseEntity.status(404).body(Map.of("message", "comment_not_found", "data", null));
        }
        comment.put("content", request.get("content"));
        comment.put("updated_at", new Date());
        return ResponseEntity.ok(Map.of("message", "comment_updated", "data", Map.of("comment_id", commentId)));
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long commentId) {
        if (!commentStore.containsKey(commentId)) {
            return ResponseEntity.status(404).body(Map.of("message", "comment_not_found", "data", null));
        }
        commentStore.remove(commentId);

        Map<String, Object> body = new HashMap<>();
        body.put("message", "comment_deleted");
        body.put("data", null);
        return ResponseEntity.ok(body);
    }
}