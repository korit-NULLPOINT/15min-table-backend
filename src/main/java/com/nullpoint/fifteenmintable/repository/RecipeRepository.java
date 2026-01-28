package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.recipe.RecipeDetailRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeFilterReqDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeListRespDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import com.nullpoint.fifteenmintable.mapper.RecipeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RecipeRepository {

    @Autowired
    private RecipeMapper recipeMapper;

    public int addRecipe(Recipe recipe) {
        return recipeMapper.addRecipe(recipe);
    }

    public List<RecipeListRespDto> getRecipeCardListByBoardId(Integer boardId, Integer userId, Integer offset, Integer limit) {
        return recipeMapper.getRecipeCardListByBoardId(boardId, userId, offset, limit);
    }

    public List<RecipeListRespDto> getRecipeCardListByUserId(Integer userId, Integer loginUserId, Integer offset, Integer limit) {
        return recipeMapper.getRecipeCardListByUserId(userId, loginUserId, offset, limit);
    }

    public List<RecipeListRespDto> getRecipeCardListByBoardIdAndFilter(Integer boardId, Integer userId, Integer limit, Integer offset, RecipeFilterReqDto filter) {
        return recipeMapper.getRecipeCardListByBoardIdAndFilter(boardId, userId, limit, offset, filter);
    }

    public int getRecipeCountByBoardIdAndFilter(Integer boardId, RecipeFilterReqDto filter) {
        return recipeMapper.getRecipeCountByBoardIdAndFilter(boardId, filter);
    }

    public int getRecipeCountByBoardId(Integer boardId) {
        return recipeMapper.getRecipeCountByBoardId(boardId);
    }

    public int getRecipeCountByUserId(Integer userId) {
        return recipeMapper.getRecipeCountByUserId(userId);
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
