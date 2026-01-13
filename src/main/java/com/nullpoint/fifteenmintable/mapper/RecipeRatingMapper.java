package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.rating.RatingSummaryRespDto;
import com.nullpoint.fifteenmintable.dto.rating.RecipeRatingRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeRating;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RecipeRatingMapper {
    int insertRating(RecipeRating recipeRating);
    int updateRating(RecipeRating recipeRating);
    int deleteRating(@Param("recipeId") Integer recipeId, @Param("userId") Integer userId);
    Optional<RecipeRatingRespDto> getRatingByRecipeIdAndUserId(@Param("recipeId") Integer recipeId,
                                                               @Param("userId") Integer userId);
    Optional<RatingSummaryRespDto> getRatingSummaryByRecipeId(Integer recipeId);
}