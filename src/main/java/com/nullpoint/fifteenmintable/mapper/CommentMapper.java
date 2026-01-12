package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {
    int addComment(Comment comment);
    int modifyComment(Comment comment);
    int deleteComment(Integer commentId);
    Optional<CommentRespDto> getCommentByCommentId(Integer commentId);
    List<CommentRespDto> getCommentList();
    List<CommentRespDto> getCommentListByUserId(Integer userId);
}
