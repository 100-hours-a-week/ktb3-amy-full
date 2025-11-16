package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.dto.comment.CommentResponse;
import kr.adapterz.amy_community.dto.comment.CreateCommentRequest;
import kr.adapterz.amy_community.dto.comment.UpdateCommentRequest;
import kr.adapterz.amy_community.entity.CommentEntity;
import kr.adapterz.amy_community.entity.PostEntity;
import kr.adapterz.amy_community.entity.UserEntity;
import kr.adapterz.amy_community.repository.CommentRepository;
import kr.adapterz.amy_community.repository.PostRepository;
import kr.adapterz.amy_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 등록
    public CommentResponse create(CreateCommentRequest req) {

        // 👇 임시 author 설정 (로그인 기능 미구현)
        UserEntity author = userRepository.findAll().stream().findFirst().orElse(null);
        if (author == null) return null;

        PostEntity post = postRepository.findById(req.getPostId()).orElse(null);
        if (post == null) return null;

        CommentEntity c = new CommentEntity();
        c.setAuthor(author);
        c.setPost(post);
        c.setContent(req.getContent());
        c.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        commentRepository.save(c);

        post.setCommentCount(post.getCommentCount() + 1);

        return new CommentResponse(c);
    }


    // 목록
    public List<CommentResponse> list(Long postId) {
        return commentRepository.findAll().stream()
                .filter(c -> c.getPost().getId().equals(postId))
                .map(CommentResponse::new)
                .toList();
    }

    // 수정
    public CommentResponse update(Long id, UpdateCommentRequest req) {
        CommentEntity c = commentRepository.findById(id).orElse(null);
        if (c == null) return null;

        c.setContent(req.getContent());
        commentRepository.save(c);

        return new CommentResponse(c);
    }

    // 삭제
    public boolean delete(Long id) {
        CommentEntity c = commentRepository.findById(id).orElse(null);
        if (c == null) return false;

        PostEntity post = c.getPost();

        commentRepository.delete(c);

        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);

        return true;
    }
}
