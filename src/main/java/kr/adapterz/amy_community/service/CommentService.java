package kr.adapterz.amy_community.service;

import kr.adapterz.amy_community.entity.Comment;
import kr.adapterz.amy_community.entity.Post;
import kr.adapterz.amy_community.entity.User;
import kr.adapterz.amy_community.repository.CommentRepository;
import kr.adapterz.amy_community.repository.PostRepository;
import kr.adapterz.amy_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment create(Long postId, Long userId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        // 댓글 생성
        Comment comment = new Comment(post, user, content);

        // 댓글 개수 증가
        post.increaseCommentCount();

        return commentRepository.save(comment);
    }

    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPost_IdOrderByIdDesc(postId);
    }

    @Transactional
    public Comment update(Long commentId, Long loginUserId, String newContent) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));

        // 작성자인지 검증
        if (!comment.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("forbidden");
        }

        comment.changeContent(newContent);
        return comment;
    }

    @Transactional
    public void delete(Long commentId, Long loginUserId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found"));

        // 작성자 검증
        if (!comment.getUser().getId().equals(loginUserId)) {
            throw new IllegalArgumentException("forbidden");
        }

        // 댓글 개수 감소
        comment.getPost().decreaseCommentCount();

        // 실제 삭제
        commentRepository.delete(comment);
    }
}