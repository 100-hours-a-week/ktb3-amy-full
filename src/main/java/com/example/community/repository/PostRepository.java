package com.example.community.repository;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class PostRepository {

    //DB 대신 HashMap 사용!
    public Map<Long, Map<String, Object>> postStore = new HashMap<>();
    public long postId = 1;

    //게시글 저장
    public Map<String, Object>save(Map<String, Object> post) {
        post.put("post_id", postId++);
        postStore.put((Long) post.get("post_id"), post);
        return post;
    }

    //전체 게시글 목록 조회
    public Collection<Map<String, Object>>findAll() {
        return postStore.values();
    }

    //게시글 상세 조회
    public Map<String, Object>findById(Long id) {
        return postStore.get(id);
    }

    //게시글 존재 여부 확인
    public boolean existById(Long id) {
        return postStore.containsKey(id);
    }

    //게시글 삭제
    public void delete(Long id) {
        postStore.remove(id);
    }

    //게시글 수정
    public void update(Long id, Map<String, Object> updated) {
        postStore.put(id, updated);
    }
}
