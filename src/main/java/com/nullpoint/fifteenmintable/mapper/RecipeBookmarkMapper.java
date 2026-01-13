package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.bookmark.BookmarkRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeBookmark;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecipeBookmarkMapper {

    int addBookmark(RecipeBookmark recipeBookmark);
    int deleteBookmark(Integer recipeId, Integer userId);
    boolean existsByRecipeIdAndUserId(Integer recipeId, Integer userId);
    List<BookmarkRespDto> getBookmarkListByUserId(Integer userId);
}
