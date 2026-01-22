package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.recipe.RecipeDetailRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeListRespDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeMapper {

    int addRecipe(Recipe recipe);
    List<RecipeListRespDto> getRecipeCardListByBoardId(
            @Param("boardId") Integer boardId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    List<RecipeListRespDto> getRecipeCardListByUserId(
            @Param("userId") Integer userId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    Optional<RecipeDetailRespDto> getRecipeDetail(@Param("boardId") Integer boardId,
                                                  @Param("recipeId") Integer recipeId);
    int getRecipeCountByBoardId(@Param("boardId") Integer boardId);
    int getRecipeCountByUserId(@Param("userId") Integer userId);
    Optional<Recipe> getRecipeEntityById(@Param("recipeId") Integer recipeId);
    int increaseViewCount(@Param("recipeId") Integer recipeId);
    int modifyRecipe(Recipe recipe);
    int removeRecipe(@Param("recipeId") Integer recipeId,
                     @Param("userId") Integer userId);
}
