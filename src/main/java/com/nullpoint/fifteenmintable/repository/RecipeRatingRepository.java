package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.rating.RatingSummaryRespDto;
import com.nullpoint.fifteenmintable.dto.rating.RecipeRatingRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeRating;
import com.nullpoint.fifteenmintable.mapper.RecipeRatingMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    public Optional<RecipeRatingRespDto> getRatingByRecipeIdAndUserId(@Param("recipeId") Integer recipeId,
                                                                      @Param("userId") Integer userId) {
        return recipeRatingMapper.getRatingByRecipeIdAndUserId(recipeId, userId);
    }

    public Optional<RatingSummaryRespDto> getRatingSummaryByRecipeId(Integer recipeId) {
        return recipeRatingMapper.getRatingSummaryByRecipeId(recipeId);
    }
}