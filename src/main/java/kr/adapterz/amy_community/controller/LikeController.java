package kr.adapterz.amy_community.controller;

import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.repository.LikeRepository;
import kr.adapterz.amy_community.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;
    private final LikeRepository likeRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> toggleLike(
            @RequestParam Long postId,
            Authentication auth
    ) {
        User user = (User) auth.getPrincipal();

        boolean liked = likeService.toggleLike(user.getId(), postId);
        long likeCount = likeRepository.countByPost_Id(postId);

        return ResponseEntity.ok(
                Map.of(
                        "liked", liked,
                        "likeCount", likeCount
                )
        );
    }
}