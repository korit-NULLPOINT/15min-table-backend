package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.post.PostDetailRespDto;
import com.nullpoint.fifteenmintable.dto.post.PostListRespDto;
import com.nullpoint.fifteenmintable.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    int addPost(Post post);

    int modifyPost(@Param("postId") Integer postId,
                   @Param("title") String title,
                   @Param("content") String content);

    int deletePost(Integer postId);

    Optional<Post> getPostById(Integer postId);

    Optional<PostDetailRespDto> getPostDetail(
            @Param("boardId") Integer boardId,
            @Param("postId") Integer postId
    );

    int increaseViewCount(Integer postId);

    List<PostListRespDto> getPostListByCursor(
            @Param("boardId") Integer boardId,
            @Param("sizePlusOne") Integer sizePlusOne,
            @Param("cursorCreateDt") LocalDateTime cursorCreateDt,
            @Param("cursorPostId") Integer cursorPostId,
            @Param("keyword") String keyword,
            @Param("searchType") String searchType // TITLE | USERNAME | null이면 ALL
    );
}
