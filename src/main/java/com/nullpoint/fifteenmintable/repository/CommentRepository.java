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

    public int addComment(Comment comment) {
        return commentMapper.addComment(comment);
    }

    public Optional<CommentRespDto> getCommentByCommentId(Integer commentId) {
        return commentMapper.getCommentByCommentId(commentId);
    }

    public  List<CommentRespDto> getCommentListByRecipeId(Integer recipeId) {
        return commentMapper.getCommentListByRecipeId(recipeId);
    }

    public List<CommentRespDto> getCommentListByUserId(Integer userId) {
        return commentMapper.getCommentListByUserId(userId);
    }

    public int deleteComment(Integer commentId) {
        return commentMapper.deleteComment(commentId);
    }
}
