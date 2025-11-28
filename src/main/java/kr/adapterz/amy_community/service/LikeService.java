package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.entity.Like;
import kr.adapterz.amy_community.entity.Post;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.repository.LikeRepository;
import kr.adapterz.amy_community.repository.PostRepository;
import kr.adapterz.amy_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean toggleLike(Long userId, Long postId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));

        // 이미 좋아요 눌렀다면 → 취소
        if (likeRepository.existsByUser_IdAndPost_Id(userId, postId)) {
            likeRepository.deleteByUser_IdAndPost_Id(userId, postId);

            // 최신 좋아요 개수 반영
            post.setLikeCount((int) likeRepository.countByPost_Id(postId));
            return false; // 좋아요 OFF
        }

        // 누르지 않았다면 → 좋아요 추가
        likeRepository.save(new Like(user, post));

        // 최신 좋아요 개수 반영
        post.setLikeCount((int) likeRepository.countByPost_Id(postId));
        return true; // 좋아요 ON
    }
}