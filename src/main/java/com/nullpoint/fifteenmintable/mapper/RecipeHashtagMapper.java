package com.nullpoint.fifteenmintable.mapper;


import com.nullpoint.fifteenmintable.entity.RecipeHashtag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeHashtagMapper {
    int createRecipeHashtag(RecipeHashtag recipeHashtag);

    List<RecipeHashtag> findAll();
    Optional<RecipeHashtag> findByRecipeHashtagId(Integer recipeHashtagId);
    Optional<List<RecipeHashtag>> findByRecipeId(Integer recipeId);
    Optional<List<RecipeHashtag>> findByHashtagId(Integer hashtagId);


    int deleteRecipeHashtag(Integer recipeHashtagId);
}
