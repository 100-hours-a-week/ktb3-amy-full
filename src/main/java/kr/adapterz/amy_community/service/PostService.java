package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.dto.PostSummaryDto;
import kr.adapterz.amy_community.entity.Post;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.repository.CommentRepository;
import kr.adapterz.amy_community.repository.LikeRepository;
import kr.adapterz.amy_community.repository.PostRepository;
import kr.adapterz.amy_community.repository.UserRepository;
import kr.adapterz.amy_community.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FileUtil fileUtil;

    private static final String UPLOAD_DIR = "/Users/sumin/amy-community/uploads/post/";

    @Transactional
    public Post create(Long userId, String title, String content, String imageBase64) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        String imageUrl = null;

        // Base64 이미지 업로드
        if (imageBase64 != null && !imageBase64.isBlank()) {

            String fileName = System.currentTimeMillis() + ".png";

            try {
                fileUtil.saveBase64Image(imageBase64, UPLOAD_DIR, fileName);
                imageUrl = "/uploads/post/" + fileName;  // 서비스에서 접근하는 경로
            } catch (Exception e) {
                throw new IllegalArgumentException("image_save_fail");
            }
        }

        Post post = new Post(title, content, imageUrl, author);
        return postRepository.save(post);
    }

    @Transactional
    public Post increaseViewAndGet(Long id) {
        Post post = findById(id);
        post.increaseViewCount();
        return post;
    }

    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
    }

    @Transactional
    public Post update(
            Long postId,
            Long loginUserId,
            String title,
            String content,
            String imageBase64,
            String originalImageUrl
    ) {

        Post post = findById(postId);

        // 본인만 수정 가능
        if (!post.getAuthor().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("forbidden");
        }

        if (title != null) post.changeTitle(title);
        if (content != null) post.changeContent(content);

        // 새 이미지 업로드
        if (imageBase64 != null && !imageBase64.isBlank()) {

            String fileName = System.currentTimeMillis() + ".png";

            try {
                fileUtil.saveBase64Image(imageBase64, UPLOAD_DIR, fileName);
                post.changeImage("/uploads/post/" + fileName);
            } catch (Exception e) {
                throw new IllegalArgumentException("image_save_fail");
            }
        }
        // 기존 이미지 유지
        else if (originalImageUrl != null) {
            post.changeImage(originalImageUrl);
        }

        return post;
    }

    @Transactional
    public void delete(Long postId, Long loginUserId) {

        Post post = findById(postId);

        // 본인 확인
        if (!post.getAuthor().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("forbidden");
        }

        // FK 관계 데이터 먼저 삭제
        likeRepository.deleteAllByPostId(postId);
        commentRepository.deleteAllByPostId(postId);

        postRepository.deleteById(postId);
    }

    public List<Post> findByTitle(String keyword) {
        return postRepository.searchByTitle(keyword);
    }

    public List<Post> findByAuthorNickname(String nickname) {
        return postRepository.findByAuthorNickname(nickname);
    }

    public List<String> findTitlesByAuthorId(Long authorId) {
        return postRepository.findTitlesByAuthorId(authorId);
    }

    public List<PostSummaryDto> findPostSummaries(String keyword) {
        return postRepository.findPostSummaries(keyword);
    }

    public List<Post> findALlPostsWithNPlusOne() {
        return postRepository.findAll();
    }

    public List<Post> findAllPostsByEntityGraph() {
        return postRepository.findAllBy();
    }

    public List<Post> searchAsList(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Page<Post> searchAsPage(String keyword, int page, int size, String sortBy, String direction) {

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);
        return postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    public Slice<Post> searchAsSlice(String keyword, int page, int size, String sortBy, String direction) {

        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(direction)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        return postRepository.findSliceByTitleContainingIgnoreCase(keyword, pageable);
    }
}