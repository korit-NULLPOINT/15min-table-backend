package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.recipe.RecipeDetailRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeListRespDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecipeRepository {

    private final RecipeMapper recipeMapper;

    public int addRecipe(Recipe recipe) {
        return recipeMapper.addRecipe(recipe);
    }

    public List<RecipeListRespDto> getRecipeCardListByBoardId(Integer boardId, Integer offset, Integer limit) {
        return recipeMapper.getRecipeCardListByBoardId(boardId, offset, limit);
    }

    public int getRecipeCountByBoardId(Integer boardId) {
        return recipeMapper.getRecipeCountByBoardId(boardId);
    }

    public Optional<RecipeDetailRespDto> getRecipeDetail(Integer boardId, Integer recipeId) {
        return recipeMapper.getRecipeDetail(boardId, recipeId);
    }

    public Optional<Recipe> getRecipeEntityById(Integer recipeId) {
        return recipeMapper.getRecipeEntityById(recipeId);
    }

    public int increaseViewCount(Integer recipeId) {
        return recipeMapper.increaseViewCount(recipeId);
    }

    public int modifyRecipe(Recipe recipe) {
        return recipeMapper.modifyRecipe(recipe);
    }

    public int removeRecipe(Integer recipeId, Integer userId) {
        return recipeMapper.removeRecipe(recipeId, userId);
    }
}
