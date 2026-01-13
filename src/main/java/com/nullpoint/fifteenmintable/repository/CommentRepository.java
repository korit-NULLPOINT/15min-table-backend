package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import com.nullpoint.fifteenmintable.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {
    @Autowired
    private CommentMapper commentMapper;

    public Optional<Comment> addComment(Comment comment) {
        try {
            int result = commentMapper.addComment(comment);
            if (result != 1) {
                throw new RuntimeException("댓글 추가 실패");
            }
        } catch (RuntimeException e) {
            return Optional.empty();
        }
        return Optional.of(comment);
    }

    public  Optional<CommentRespDto> getCommentByCommentId(Integer commentId) {
        return commentMapper.getCommentByCommentId(commentId);
    }

    public List<CommentRespDto> getCommentList() {
        return commentMapper.getCommentList();
    }

    public List<CommentRespDto> getCommentListByUserId(Integer userId) {
        return commentMapper.getCommentListByUserId(userId);
    }

    public int modifyComment(Comment comment) {
        return commentMapper.modifyComment(comment);
    }

    public int deleteComment(Integer commentId) {
        return commentMapper.deleteComment(commentId);
    }
}
