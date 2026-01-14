package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.entity.RecipeHashtag;
import com.nullpoint.fifteenmintable.mapper.RecipeHashtagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RecipeHashtagRepository {

    @Autowired
    private RecipeHashtagMapper recipeHashtagMapper;

    public int createRecipeHashtag(RecipeHashtag recipeHashtag) {
        return recipeHashtagMapper.createRecipeHashtag(recipeHashtag);
    }

    public int deleteAllByRecipeId(Integer recipeId) {
        return recipeHashtagMapper.deleteAllByRecipeId(recipeId);
    }

    public int deleteByRecipeIdAndHashtagId(Integer recipeId, Integer hashtagId) {
        return recipeHashtagMapper.deleteByRecipeIdAndHashtagId(recipeId, hashtagId);
    }

    public Optional<RecipeHashtag> getByRecipeHashtagId(Integer recipeHashtagId) {
        return recipeHashtagMapper.getByRecipeHashtagId(recipeHashtagId);
    }

    public List<RecipeHashtag> getByRecipeId(Integer recipeId) {
        return recipeHashtagMapper.getByRecipeId(recipeId);
    }

    public List<RecipeHashtag> getByHashtagId(Integer hashtagId) {
        return recipeHashtagMapper.getByHashtagId(hashtagId);
    }
}
