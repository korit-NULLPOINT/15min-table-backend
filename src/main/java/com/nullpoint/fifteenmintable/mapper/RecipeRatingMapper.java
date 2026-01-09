package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.RecipeRating;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeRatingMapper {
    int createRecipeRating(RecipeRating recipeRating);
    
}
