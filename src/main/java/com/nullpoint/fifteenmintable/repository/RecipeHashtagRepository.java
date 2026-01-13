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
    private RecipeHashtagMapper hashtagMapper;

    public int createRecipeHashtag(RecipeHashtag recipeHashtag) {
        return hashtagMapper.createRecipeHashtag(recipeHashtag);
    }

    public int deleteAllByRecipeId(Integer recipeId) {
        return hashtagMapper.deleteAllByRecipeId(recipeId);
    }

    public int deleteByRecipeIdAndHashtagId(Integer recipeId, Integer hashtagId) {
        return hashtagMapper.deleteByRecipeIdAndHashtagId(recipeId, hashtagId);
    }

    public Optional<RecipeHashtag> getByRecipeHashtagId(Integer recipeHashtagId) {
        return hashtagMapper.getByRecipeHashtagId(recipeHashtagId);
    }

    public List<RecipeHashtag> getByRecipeId(Integer recipeId) {
        return hashtagMapper.getByRecipeId(recipeId);
    }

    public List<RecipeHashtag> getByHashtagId(Integer hashtagId) {
        return hashtagMapper.getByHashtagId(hashtagId);
    }
}
