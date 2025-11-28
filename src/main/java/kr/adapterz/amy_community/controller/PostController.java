package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.dto.PostSummaryDto;
import kr.adapterz.amy_community.entity.Post;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponse create(
            @RequestBody CreatePostRequest request,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        Post post = postService.create(
                user.getId(),
                request.title,
                request.content,
                request.imageBase64
        );

        return PostResponse.of(post);
    }

    @GetMapping("/{id}")
    public PostResponse get(@PathVariable Long id) {
        Post post = postService.increaseViewAndGet(id);
        return PostResponse.of(post);
    }

    @PatchMapping("/{id}")
    public PostResponse update(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        Post updatedPost = postService.update(
                id,
                user.getId(),
                request.title,
                request.content,
                request.imageBase64,
                request.originalImageUrl
        );

        return PostResponse.of(updatedPost);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();
        postService.delete(id, user.getId());
    }

    @GetMapping("/search/title/keyword")
    public List<PostResponse> searchByTitle(@RequestParam String keyword) {
        return postService.findByTitle(keyword).stream()
                .map(PostResponse::of)
                .toList();
    }

    @GetMapping("/search/author/nickname")
    public List<PostResponse> byAuthor(@RequestParam String nickname) {
        return postService.findByAuthorNickname(nickname).stream()
                .map(PostResponse::of)
                .toList();
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
        return postService.findALlPostsWithNPlusOne().stream()
                .map(PostResponse::of)
                .toList();
    }

    @GetMapping("/all/entity-graph")
    public List<PostResponse> allWithEntityGraph() {
        return postService.findAllPostsByEntityGraph().stream()
                .map(PostResponse::of)
                .toList();
    }

    @GetMapping("/search/list")
    public List<PostResponse> searchAsList(@RequestParam String keyword) {
        return postService.searchAsList(keyword).stream()
                .map(PostResponse::of)
                .toList();
    }

    @GetMapping("/search/page")
    public Page<PostResponse> searchAsPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return postService.searchAsPage(keyword, page, size, sortBy, direction)
                .map(PostResponse::of);
    }

    @GetMapping("/search/slice")
    public Slice<PostResponse> searchAsSlice(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return postService.searchAsSlice(keyword, page, size, sortBy, direction)
                .map(PostResponse::of);
    }

    @Data
    public static class UpdatePostRequest {
        private String title;
        private String content;
        private String imageBase64;
        private String originalImageUrl;
    }

    @Data
    public static class CreatePostRequest {
        private String title;
        private String content;
        private String imageBase64;
    }

    @Data
    public static class PostResponse {
        private Long id;
        private String title;
        private String content;
        private String imageUrl;

        private Long authorId;
        private String authorNickname;

        private int viewCount;
        private int likeCount;
        private int commentCount;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public static PostResponse of(Post post) {
            return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getAuthor().getId(),
                    post.getAuthor().getNickname(),
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getCommentCount(),
                    post.getCreatedAt(),
                    post.getUpdatedAt(),
                    post.getCreatedBy(),
                    post.getUpdatedBy()
            );
        }

        public PostResponse(
                Long id,
                String title,
                String content,
                String imageUrl,
                Long authorId,
                String authorNickname,
                int viewCount,
                int likeCount,
                int commentCount,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                String createdBy,
                String updatedBy
        ) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.imageUrl = imageUrl;
            this.authorId = authorId;
            this.authorNickname = authorNickname;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.createdBy = createdBy;
            this.updatedBy = updatedBy;
        }
    }
}