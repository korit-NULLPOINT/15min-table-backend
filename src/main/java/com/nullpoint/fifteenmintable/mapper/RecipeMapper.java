package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeMapper {
    int createRecipe(Recipe recipe);

    List<Recipe> findAll();
    Optional<Recipe> findByRecipeId(Integer recipeId);
    Optional<List<Recipe>> findByUserId(Integer userId);
    Optional<List<Recipe>> findByMainCategoryId(Integer mainCategoryId);
    Optional<List<Recipe>> findBySubCategoryId(Integer subCategoryId);
    Optional<List<Recipe>> findByKeyword(String keyword);

    int updateRecipe(Recipe recipe);

    int deleteRecipe(Integer recipeId);
}
