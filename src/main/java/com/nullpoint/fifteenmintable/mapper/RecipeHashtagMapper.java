package com.nullpoint.fifteenmintable.mapper;


import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeHashtag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeHashtagMapper {
    int createRecipeHashtag(RecipeHashtag recipeHashtag);
    int deleteAllByRecipeId(Integer recipeId);
    int deleteByRecipeIdAndHashtagId(Integer recipeId, Integer hashtagId);
    Optional<RecipeHashtag> getByRecipeHashtagId(Integer recipeHashtagId);
    // 내부 디버깅/검증용으로 recipe_hashtag_id로 단건 조회가 필요할 때
    List<HashtagRespDto> getByRecipeId(Integer recipeId);
    // 게시물 상세 페이지에서 해시태그 목록
    List<RecipeHashtag> getByHashtagId(Integer hashtagId);
    // 역으로 해시태그로 게시글 목록 (동일 해시태그 쓰일때)
}
