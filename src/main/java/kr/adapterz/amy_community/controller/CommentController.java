package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.dto.comment.CommentResponse;
import kr.adapterz.amy_community.dto.comment.CreateCommentRequest;
import kr.adapterz.amy_community.dto.comment.UpdateCommentRequest;
import kr.adapterz.amy_community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public Map<String, Object> create(@RequestBody CreateCommentRequest req) {
        CommentResponse data = commentService.create(req);

        return (data == null)
                ? Map.of("message", "fail")
                : Map.of("message", "comment_created", "data", data);
    }

    // 댓글 목록
    @GetMapping("/post/{postId}")
    public Map<String, Object> list(@PathVariable Long postId) {
        return Map.of(
                "message", "comment_list",
                "data", commentService.list(postId)
        );
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest req) {

        CommentResponse data = commentService.update(id, req);

        return (data == null)
                ? Map.of("message", "comment_not_found")
                : Map.of("message", "comment_updated", "data", data);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = commentService.delete(id);

        return Map.of("message", ok ? "comment_deleted" : "comment_not_found");
    }
}
