package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.dto.post.PostResponse;
import kr.adapterz.amy_community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping(consumes = "multipart/form-data")
    public Map<String, Object> create(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        PostResponse data = postService.createPost(title, content, image);
        return Map.of("message", "post_created", "data", data);
    }

    // 목록 조회 (Slice)
    @GetMapping("/search/slice")
    public Map<String, Object> getPostSlice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Slice<PostResponse> slice = postService.getPostSlice(page, size);

        return Map.of(
                "message", "post_list",
                "data", Map.of(
                        "content", slice.getContent(),
                        "last", slice.isLast()
                )
        );
    }

    // 상세 조회
    @GetMapping("/{id}")
    public Map<String, Object> getPost(@PathVariable Long id) {
        PostResponse data = postService.getPost(id);
        return (data == null)
                ? Map.of("message", "post_not_found")
                : Map.of("message", "post_detail", "data", data);
    }

    // 수정
    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        PostResponse data = postService.updatePost(id, title, content, image);

        return Map.of(
                "message", "post_updated",
                "data", data
        );
    }

    // 삭제
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean ok = postService.delete(id);
        return Map.of("message", ok ? "post_deleted" : "post_not_found");
    }

    // 좋아요
    @PostMapping("/{id}/like")
    public Map<String, Object> like(@PathVariable Long id) {
        PostResponse data = postService.toggleLike(id);
        return (data == null)
                ? Map.of("message", "post_not_found")
                : Map.of("message", "like_toggled", "data", data);
    }
}
