package com.nullpoint.fifteenmintable.repository;

import com.nullpoint.fifteenmintable.dto.bookmark.BookmarkRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeBookmark;
import com.nullpoint.fifteenmintable.mapper.RecipeBookmarkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecipeBookmarkRepository {
    @Autowired
    private RecipeBookmarkMapper recipeBookmarkMapper;

    public int addBookmark(RecipeBookmark recipeBookmark) {
        return recipeBookmarkMapper.addBookmark(recipeBookmark);
    }

    public int deleteBookmark(Integer recipeId, Integer userId) {
        return recipeBookmarkMapper.deleteBookmark(recipeId, userId);
    }

    public boolean existsByRecipeIdAndUserId(Integer recipeId, Integer userId) {
        return recipeBookmarkMapper.existsByRecipeIdAndUserId(recipeId, userId);
    }

    public List<BookmarkRespDto> getBookmarkListByUserId(Integer userId) {
        return recipeBookmarkMapper.getBookmarkListByUserId(userId);
    }

}
