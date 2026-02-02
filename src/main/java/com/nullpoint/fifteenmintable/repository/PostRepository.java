package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.post.PostDetailRespDto;
import com.nullpoint.fifteenmintable.dto.post.PostListRespDto;
import com.nullpoint.fifteenmintable.entity.Post;
import com.nullpoint.fifteenmintable.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    @Autowired
    private PostMapper postMapper;

    public int addPost(Post post) {
        return postMapper.addPost(post);
    }

    public int modifyPost(Integer postId, String title, String content) {
        return postMapper.modifyPost(postId, title, content);
    }

    public int deletePost(Integer postId) {
        return postMapper.deletePost(postId);
    }

    public Optional<Post> getPostById(Integer postId) {
        return postMapper.getPostById(postId);
    }

    public Optional<PostDetailRespDto> getPostDetail(Integer boardId, Integer postId) {
        return postMapper.getPostDetail(boardId, postId);
    }

    public int increaseViewCount(Integer postId) {
        return postMapper.increaseViewCount(postId);
    }

    public List<PostListRespDto> getPostListByCursor(
            Integer boardId, Integer sizePlusOne, LocalDateTime cursorCreateDt,
            Integer cursorPostId, String keyword, String searchType
    ) {
        return postMapper.getPostListByCursor(
                boardId, sizePlusOne, cursorCreateDt,
                cursorPostId, keyword, searchType
        );
    }
}
