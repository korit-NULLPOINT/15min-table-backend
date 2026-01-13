package com.nullpoint.fifteenmintable.mapper;


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
    List<RecipeHashtag> getByRecipeId(Integer recipeId);
    List<RecipeHashtag> getByHashtagId(Integer hashtagId);

}
