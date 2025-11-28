package kr.adapterz.amy_community.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import kr.adapterz.amy_community.entity.Comment;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @Valid @RequestBody CommentCreateRequest request,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal(); // JWT 인증된 사용자

        Comment saved = commentService.create(
                request.getPostId(),
                user.getId(),
                request.getContent()
        );

        return ResponseEntity.ok(CommentResponse.of(saved));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {

        List<CommentResponse> list = commentService.findByPostId(postId)
                .stream()
                .map(CommentResponse::of)
                .toList();

        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        Comment updated = commentService.update(
                commentId,
                user.getId(),     // 수정 요청자
                request.getContent()
        );

        return ResponseEntity.ok(CommentResponse.of(updated));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        commentService.delete(commentId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CommentCreateRequest {
        private Long postId;
        private String content;
    }

    @Data
    public static class CommentUpdateRequest {
        private String content;
    }

    @Data
    public static class CommentResponse {
        private Long id;
        private Long postId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static CommentResponse of(Comment comment) {
            CommentResponse dto = new CommentResponse();
            dto.id = comment.getId();
            dto.postId = comment.getPost().getId();
            dto.userId = comment.getUser().getId();
            dto.content = comment.getContent();
            dto.createdAt = comment.getCreatedAt();
            dto.updatedAt = comment.getUpdatedAt();
            return dto;
        }
    }
}