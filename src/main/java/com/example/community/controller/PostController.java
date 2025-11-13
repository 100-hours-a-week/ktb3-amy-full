package com.example.community.controller;

import com.example.community.dto.PostResponse;
import com.example.community.dto.PostSummaryDto;
import com.example.community.entity.PostEntity;
import com.example.community.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Map<String, Object> create(@RequestBody CreatePostRequest request) {
        PostEntity postEntity = postService.create(request.authorId, request.title, request.content);

        return Map.of(
                "message", "post_created",
                "data", PostResponse.of(postEntity)
        );
    }

    @GetMapping
    public Map<String, Object> getAllPosts(@RequestParam(defaultValue = "1") int page) {
        List<PostResponse> posts = postService.findAllPosts(page).stream()
                .map(PostResponse::of)
                .toList();

        return Map.of(
                "message", "post_list",
                "data", posts
        );
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        return PostResponse.of(postService.findById(id));
    }

    @PatchMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @RequestBody UpdatePostRequest request) {
        PostEntity updatedPost = postService.update(id, request.title, request.content);
        return PostResponse.of(updatedPost);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }

    @GetMapping("/search/title/keyword")
    public List<PostResponse> searchByTitle(@RequestParam String keyword) {
        return postService.findByTitle(keyword).stream().map(PostResponse::of).toList();
    }


    @GetMapping("/search/author/nickname")
    public List<PostResponse> byAuthor(@RequestParam String nickname) {
        return postService.findByAuthorNickname(nickname).stream().map(PostResponse::of).toList();
    }

    @GetMapping("/title/author/{authorId}")
    public List<String> getTitlesByAuthor(@PathVariable Long authorId) {
        return postService.findTitlesByAuthorId(authorId);
    }

    @GetMapping("/summaries/keyword")
    public List<PostSummaryDto> summaryByTitle(@RequestParam String keyword) {
        return postService.findPostSummaries(keyword);
    }

    @GetMapping("/all/n-plus-one")
    public List<PostResponse> allWithNPlusOne() {
        return postService.findALlPostsWithNPlusOne().stream().map(PostResponse::of).toList();
    }

    @GetMapping("/all/entity-graph")
    public List<PostResponse> allWithEntityGraph() {
        return postService.findAllPostsByEntityGraph().stream().map(PostResponse::of).toList();
    }

    // pageable
    @GetMapping("/search/list")
    public List<PostResponse> searchAsList(@RequestParam String keyword) {
        return postService.searchAsList(keyword).stream().map(PostResponse::of).toList();
    }

    @GetMapping("/search/page")
    public Page<PostResponse> searchAsPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postId") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return postService.searchAsPage(keyword, page, size, sortBy, direction).map(PostResponse::of);
    }

    @GetMapping("/search/slice")
    public Slice<PostResponse> searchAsSlice(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postId") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return postService.searchAsSlice(keyword, page, size, sortBy, direction).map(PostResponse::of);
    }

    @Data
    public static class UpdatePostRequest {
        private String title;
        private String content;
    }

    @Data
    public static class CreatePostRequest {
        private Long authorId;
        private String title;
        private String content;
    }

    @Data
    public static class PostResponse {
        private Long id;
        private String title;
        private String content;
        private Long authorId;
        private String authorNickname;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public static PostResponse of(PostEntity postEntity) {
            return new PostResponse(postEntity.getPostId(), postEntity.getTitle(), postEntity.getContent(),
                    postEntity.getAuthor().getId(), postEntity.getAuthor().getNickname(),
                    postEntity.getCreatedAt(),
                    postEntity.getUpdatedAt(),
                    postEntity.getCreatedBy(),
                    postEntity.getUpdatedBy());
        }

        public PostResponse(Long id, String title, String content, Long authorId, String authorNickname,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt,
                            String createdBy,
                            String updatedBy) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authorId = authorId;
            this.authorNickname = authorNickname;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.createdBy = createdBy;
            this.updatedBy = updatedBy;
        }
    }
}