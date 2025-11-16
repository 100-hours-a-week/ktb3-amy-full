package kr.adapterz.amy_community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.adapterz.amy_community.dto.post.PostResponse;
import kr.adapterz.amy_community.entity.PostEntity;
import kr.adapterz.amy_community.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    // 생성
    public PostResponse createPost(String title, String content, MultipartFile image) {

        PostEntity post = new PostEntity();
        post.setTitle(title);
        post.setContent(content);
        post.setLikes(0);
        post.setViews(0);
        post.setCommentCount(0);

        post.setCreatedAt(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (image != null && !image.isEmpty()) {
            try {
                String encoded = Base64.getEncoder().encodeToString(image.getBytes());
                post.setImage(encoded);
            } catch (Exception e) {
                post.setImage(null);
            }
        }

        postRepository.save(post);
        return new PostResponse(post);
    }

    // 목록 조회 (Slice)
    public Slice<PostResponse> getPostSlice(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable).map(PostResponse::new);
    }

    // 상세
    public PostResponse getPost(Long id) {
        return postRepository.findById(id)
                .map(PostResponse::new)
                .orElse(null);
    }

    // 수정 (이미지 선택한 경우만 변경)
    public PostResponse updatePost(Long id, String title, String content, MultipartFile image) {

        PostEntity post = postRepository.findById(id).orElse(null);
        if (post == null) return null;

        post.setTitle(title);
        post.setContent(content);

        if (image != null && !image.isEmpty()) {
            try {
                String encoded = Base64.getEncoder().encodeToString(image.getBytes());
                post.setImage(encoded);
            } catch (Exception ignored) {}
        }

        postRepository.save(post);

        return new PostResponse(post);
    }


    // 삭제
    public boolean delete(Long id) {
        PostEntity post = postRepository.findById(id).orElse(null);
        if (post == null) return false;

        postRepository.delete(post);
        return true;
    }

    // 좋아요
    public PostResponse toggleLike(Long id) {
        PostEntity post = postRepository.findById(id).orElse(null);
        if (post == null) return null;

        post.setLikes(post.getLikes() + 1);
        return new PostResponse(post);
    }
}
