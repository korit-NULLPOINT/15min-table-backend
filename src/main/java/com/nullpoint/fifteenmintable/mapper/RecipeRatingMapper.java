package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.RecipeRating;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeRatingMapper {
    int createRecipeRating(RecipeRating recipeRating);

    Optional<List<RecipeRating>> findAll();
    Optional<List<RecipeRating>> findByRecipeId(Integer recipeId);
    Optional<List<RecipeRating>> findByUserId(Integer userId);
    Optional<List<RecipeRating>> findByRating(Integer rating);

    int updateRecipeRating(RecipeRating recipeRating);

    int deleteRecipeRating(int recipeRatingId);
}
