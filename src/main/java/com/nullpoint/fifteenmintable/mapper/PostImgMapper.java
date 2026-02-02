package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.PostImg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostImgMapper {

    int addPostImgs(List<PostImg> postImgs);

    int deleteByPostId(Integer postId);

    List<String> getImgUrlsByPostId(Integer postId);
}
