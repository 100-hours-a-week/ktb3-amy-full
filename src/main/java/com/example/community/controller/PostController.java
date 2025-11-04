package com.example.community.controller;

import com.example.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    //PostService 주입
    public PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

    //게시글 작성 API
    @Operation(summary = "게시글 작성", description = "새로운 게시글을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "post_created"),
            @ApiResponse(responseCode = "400", description = "invalid_request")
    })

    //게시글 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(@RequestBody Map<String, String> request) {
        //요청을 Service에 전달하고 결과 반환
        return postService.createPost(request);
    }

    //게시글 전체 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPosts() {
        //Service 호출
        return postService.AllPosts();
    }

    //게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPostDetail(@PathVariable Long postId) {
        return postService.PostDetail(postId);
    }

    //게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> updatePost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        return postService.updatePost(postId, request);
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }
}