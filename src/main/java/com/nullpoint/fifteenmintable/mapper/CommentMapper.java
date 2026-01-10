package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
    int addComment(Comment comment);
}
