package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.bookmark.BookmarkRespDto;
import com.nullpoint.fifteenmintable.entity.RecipeBookmark;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeBookmarkMapper {
    int addBookmark(RecipeBookmark recipeBookmark);
    int deleteBookmark(Integer recipeId, Integer userId);
    Optional<BookmarkRespDto> getBookmarkByUserId(Integer recipeId, Integer userId);
    Optional<BookmarkRespDto> getBookmarkByRecipeId(Integer recipeId);
    List<BookmarkRespDto> getBookmarkListByUserId(Integer userId);
}
