package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.Hashtag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface HashtagMapper {
    int createHashtag(Hashtag hashtag);
    int deleteHashtag(Integer hashtagId);
    Optional<Hashtag> getByHashtagId(Integer hashtagId);
    List<Hashtag> getHashtagList();
    List<Hashtag> getByKeyword(String keyword);
    List<Hashtag> getHashtagsByRecipeId(Integer recipeId);


}
