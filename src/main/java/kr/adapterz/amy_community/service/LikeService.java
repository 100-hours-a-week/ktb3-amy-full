package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.entity.PostEntity;
import kr.adapterz.amy_community.entity.PostLikeEntity;
import kr.adapterz.amy_community.entity.UserEntity;
import kr.adapterz.amy_community.repository.PostLikeRepository;
import kr.adapterz.amy_community.repository.PostRepository;
import kr.adapterz.amy_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 좋아요 토글
    public Map<String, Object> toggle(Long postId, Long userId) {

        PostEntity post = postRepository.findById(postId).orElse(null);
        UserEntity user = userRepository.findById(userId).orElse(null);

        if (post == null || user == null) {
            return Map.of("message", "fail");
        }

        PostLikeEntity like =
                postLikeRepository.findByPostIdAndUserId(postId, userId);

        // 이미 좋아요 눌렀다면 → 취소
        if (like != null) {
            postLikeRepository.delete(like);
            post.setLikes(post.getLikes() - 1);
            postRepository.save(post);

            return Map.of(
                    "message", "like_off",
                    "data", Map.of("likes", post.getLikes(), "liked", false)
            );
        }

        // 좋아요 추가
        PostLikeEntity newLike = new PostLikeEntity();
        newLike.setPost(post);
        newLike.setUser(user);
        postLikeRepository.save(newLike);

        post.setLikes(post.getLikes() + 1);
        postRepository.save(post);

        return Map.of(
                "message", "like_on",
                "data", Map.of("likes", post.getLikes(), "liked", true)
        );
    }
}
