package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.Hashtag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface HashtagMapper {
    int createHashtag(Hashtag hashtag);

    List<Hashtag> findAll();
    Optional<Hashtag> findByHashtagId(Integer hashtagId);
    Optional<List<Hashtag>> findByKeyword(String keyword);

    int deleteHashtag(Integer hashtagId);
}
