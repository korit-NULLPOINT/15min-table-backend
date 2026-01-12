package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.RecipeRating;
import com.nullpoint.fifteenmintable.mapper.RecipeRatingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RecipeRatingRepository {

    @Autowired
    private RecipeRatingMapper recipeRatingMapper;

    public int insertRating(RecipeRating recipeRating) {
        return recipeRatingMapper.insertRating(recipeRating);
    }

    public int updateRating(RecipeRating recipeRating) {
        return recipeRatingMapper.updateRating(recipeRating);
    }

    public int deleteRating(Integer recipeId, Integer userId) {
        return recipeRatingMapper.deleteRating(recipeId, userId);
    }
}
