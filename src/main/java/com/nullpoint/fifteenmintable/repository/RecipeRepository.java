package com.nullpoint.fifteenmintable.repository;

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

    public int createRecipe(Recipe recipe) {
        return recipeMapper.createRecipe(recipe);
    }

    public List<Recipe> findAll() {
        return recipeMapper.findAll();
    }

    public Optional<Recipe> findByRecipeId(Integer recipeId) {
        return recipeMapper.findByRecipeId(recipeId);
    }

    public Optional<List<Recipe>> findByUserId(Integer userId) {
        return Optional.of(recipeMapper.findByUserId(userId));
    }

    public Optional<List<Recipe>> findByMainCategoryId(Integer mainCategoryId) {
        return Optional.of(recipeMapper.findByMainCategoryId(mainCategoryId));
    }

    public Optional<List<Recipe>> findBySubCategoryId(Integer subCategoryId) {
        return Optional.of(recipeMapper.findBySubCategoryId(subCategoryId));
    }

    public Optional<List<Recipe>> findByKeyword(String keyword) {
        return Optional.of(recipeMapper.findByKeyword(keyword));
    }

    public int updateRecipe(Recipe recipe) {
        return recipeMapper.updateRecipe(recipe);
    }

    public int deleteRecipe(Integer recipeId) {
        return recipeMapper.deleteRecipe(recipeId);
    }
}