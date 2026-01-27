package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.comment.CommentRespDto;
import com.nullpoint.fifteenmintable.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {
    int addComment(Comment comment);
    int deleteComment(Integer commentId);
    int deleteByTarget(@Param("targetType") String targetType, @Param("targetId") Integer targetId);
    Optional<CommentRespDto> getCommentByCommentId(Integer commentId);
    List<CommentRespDto> getCommentListByTarget(@Param("targetType") String targetType, @Param("targetId") Integer targetId);
    List<CommentRespDto> getCommentListByUserId(Integer userId);
}
