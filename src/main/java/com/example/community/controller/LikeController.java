package com.example.community.controller;

import com.example.community.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts/{postId}/likes")
public class LikeController {

    public LikeService likeService;

    //생성자 주입
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 좋아요 추가
    @PostMapping
    public ResponseEntity<Map<String, Object>> addLike(@PathVariable Long postId) {
        return likeService.addLike(postId);
    }

    // 좋아요 취소
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> removeLike(@PathVariable Long postId) {
        return likeService.removeLike(postId);
    }
}