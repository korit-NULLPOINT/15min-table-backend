package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.bookmark.BookmarkRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeBookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecipeBookmarkMapper {

    int addBookmark(RecipeBookmark recipeBookmark);
    int deleteBookmark(@Param("recipeId") Integer recipeId, @Param("userId") Integer userId);
    boolean existsByRecipeIdAndUserId(@Param("recipeId") Integer recipeId, @Param("userId") Integer userId);
    List<BookmarkRespDto> getBookmarkListByUserId(Integer userId);
}
