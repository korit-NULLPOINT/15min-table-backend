package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.PostImg;
import com.nullpoint.fifteenmintable.mapper.PostImgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostImgRepository {

    @Autowired
    private PostImgMapper postImgMapper;

    public int addPostImgs(List<PostImg> postImgs) {
        return postImgMapper.addPostImgs(postImgs);
    }

    public int deleteByPostId(Integer postId) {
        return postImgMapper.deleteByPostId(postId);
    }

    public List<String> getImgUrlsByPostId(Integer postId) {
        return postImgMapper.getImgUrlsByPostId(postId);
    }
}
