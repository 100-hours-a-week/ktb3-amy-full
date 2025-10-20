package com.example.community.repository;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CommentRepository {

    //DB 대신 HashMap 사용!
    public Map<Long, Map<String, Object>> commentStore = new HashMap<>();
    public long commentId = 1;

    //댓글 저장
    public Map<String, Object> save(Map<String, Object> commentData) {
        // commentId를 key로 저장
        commentData.put("comment_id", commentId);
        commentStore.put(commentId, commentData);

        //응답용 데이터 구성
        Map<String, Object> response = Map.of("comment_id", commentId++);
        return response;
    }

    //게시글 ID로 댓글 목록 조회
    public List<Map<String, Object>> findByPostId(Long postId) {
        List<Map<String, Object>> comments = new ArrayList<>(); //리스트 생성
        for (Map<String, Object> comment : commentStore.values()) { //전체 댓글 순회
            if (Objects.equals(comment.get("post_id"), postId)) { //postId 일치 시
                comments.add(comment); //추가
            }
        }
        return comments;
    }

    //댓글 ID로 상세 조회
    public Map<String, Object> findById(Long commentId) {
        return commentStore.get(commentId); // ID로 Map에서 조회
    }

    // 댓글 수정
    public void update(Long commentId, Map<String, Object> updatedComment) {
        commentStore.put(commentId, updatedComment);
    }

    //댓글 삭제
    public void delete(Long commentId) {
        commentStore.remove(commentId); //해당 ID 댓글 삭제
    }

    //댓글 존재 여부 확인
    public boolean existsById(Long commentId) {
        return commentStore.containsKey(commentId);
    }
}
