package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    // 좋아요 토글
    @PostMapping
    public Map<String, Object> toggle(
            @RequestParam Long postId,
            @RequestParam Long userId) {

        return likeService.toggle(postId, userId);
    }
}
