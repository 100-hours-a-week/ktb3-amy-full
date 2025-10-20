package com.example.community.service;

import com.example.community.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    //Repository 주입
    private final CommentRepository commentRepository;
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    //댓글 작성
    public ResponseEntity<Map<String, Object>> createComment(Long postId, Map<String, String> request) {

        //content 추출
        String content = request.get("content");

        //400
        if (content == null || content.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "invalid_request", "data", null));
        }

        //댓글 데이터 생성
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("post_id", postId);
        commentData.put("content", content);
        commentData.put("author", "amy");
        commentData.put("date", new Date());

        //Repository에 저장
        Map<String, Object> saved = commentRepository.save(commentData);

        //201
        return ResponseEntity.status(201).body(Map.of("message", "comment_created", "data", saved));
    }

    //댓글 목록 조회
    public ResponseEntity<Map<String, Object>> getComment(Long postId) {
        //Repository에서 해당 postId의 댓글 목록 조회
        List<Map<String, Object>> comments = commentRepository.findByPostId(postId);

        //조회 결과 반환
        return ResponseEntity.ok(Map.of("message", "comment_list", "data", comments));
    }

    //댓글 수정
    public ResponseEntity<Map<String, Object>> updateComment(Long commentId, Map<String, String> request) {
        //Repository에서 기존 댓글 조회
        Map<String, Object> comment = commentRepository.findById(commentId);

        //404
        if (comment == null) {
            return ResponseEntity.status(404).body(Map.of("message", "comment_not_found", "data", null));
        }

        //수정할 내용 추출
        String content = request.get("content");

        //기존 댓글 복사 후 수정 적용
        Map<String, Object> updatedComment = new HashMap<>(comment);
        updatedComment.put("content", content);
        updatedComment.put("updated_at", new Date()); //수정 시각 기록

        //Repository에 반영
        commentRepository.update(commentId, updatedComment);

        //수정 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "comment_updated", "data", Map.of("comment_id", commentId)));
    }

    //댓글 삭제
    public ResponseEntity<Map<String, Object>> deleteComment(Long commentId) {
        //404
        if (!commentRepository.existsById(commentId)) {
            return ResponseEntity.status(404).body(Map.of("message", "comment_not_found", "data", null));
        }

        //존재할 경우 삭제
        commentRepository.delete(commentId);

        //삭제 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "comment_deleted", "data", null));
    }
}