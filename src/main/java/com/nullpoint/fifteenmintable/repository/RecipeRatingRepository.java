package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.RecipeRating;
import com.nullpoint.fifteenmintable.mapper.RecipeRatingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecipeRatingRepository {
    private final RecipeRatingMapper recipeRatingMapper;

    public int createRecipeRating(RecipeRating recipeRating) {
        return recipeRatingMapper.createRecipeRating(recipeRating);
    }

    public Optional<List<RecipeRating>> findAll() {
        return recipeRatingMapper.findAll();
    }

    public Optional<List<RecipeRating>> findByRecipeId(Integer recipeId) {
        return recipeRatingMapper.findByRecipeId(recipeId);
    }

    public Optional<List<RecipeRating>> findByUserId(Integer userId) {
        return recipeRatingMapper.findByUserId(userId);
    }

    public Optional<List<RecipeRating>> findByRating(Integer rating) {
        return recipeRatingMapper.findByRating(rating);
    }

    public int updateRecipeRating(RecipeRating recipeRating) {
        return recipeRatingMapper.updateRecipeRating(recipeRating);
    }

    public int deleteRecipeRating(int recipeRatingId) {
        return recipeRatingMapper.deleteRecipeRating(recipeRatingId);
    }

}
