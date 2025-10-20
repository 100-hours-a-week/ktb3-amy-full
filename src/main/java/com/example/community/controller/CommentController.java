package com.example.community.controller;

import com.example.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    //Service 계층 주입
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //댓글 작성 API
    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "comment_created"),
            @ApiResponse(responseCode = "400", description = "invalid_request")
    })

    //댓글 작성
    @PostMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> createComment(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        return commentService.createComment(postId, request);
    }

    //댓글 목록 조회 API
    @Operation(summary = "댓글 목록 조회", description = "게시글에 달린 모든 댓글을 조회합니다.")

    //댓글 목록 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> getComment(@PathVariable Long postId) {
        return commentService.getComment(postId);
    }

    //댓글 수정 API
    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "comment_updated"),
            @ApiResponse(responseCode = "404", description = "comment_not_found")
    })

    //댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable Long commentId, @RequestBody Map<String, String> request) {
        return commentService.updateComment(commentId, request);
    }

    //댓글 삭제 API
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "comment_deleted"),
            @ApiResponse(responseCode = "404", description = "comment_not_found")
    })

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}