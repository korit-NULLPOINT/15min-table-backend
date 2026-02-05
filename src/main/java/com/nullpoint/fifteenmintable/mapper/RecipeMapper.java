package com.nullpoint.fifteenmintable.mapper;

import com.nullpoint.fifteenmintable.dto.recipe.RecipeDetailRespDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeFilterReqDto;
import com.nullpoint.fifteenmintable.dto.recipe.RecipeListRespDto;
import com.nullpoint.fifteenmintable.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RecipeMapper {

    int addRecipe(Recipe recipe);
    List<RecipeListRespDto> getRecipeCardListByBoardId(
            @Param("boardId") Integer boardId,
            @Param("userId") Integer userId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    List<RecipeListRespDto> getRecipeCardListByUserId(
            @Param("userId") Integer userId,
            @Param("loginUserId") Integer loginUserId, // 북마크 유무 확인용 로그인한 userId;
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );
    List<RecipeListRespDto> getRecipeCardListByBoardIdAndFilter(
            @Param("boardId") Integer boardId,
            @Param("userId") Integer userId,
            @Param("limit") int limit,
            @Param("offset") int offset,
            @Param("filter") RecipeFilterReqDto filter // DTO를 통째로 넘김
    );

    int getRecipeCountByBoardIdAndFilter(
            @Param("boardId") Integer boardId,
            @Param("filter") RecipeFilterReqDto filter // DTO를 통째로 넘김
    );

    Optional<RecipeDetailRespDto> getRecipeDetail(@Param("boardId") Integer boardId,
                                                  @Param("recipeId") Integer recipeId);
    int getRecipeCountByBoardId(@Param("boardId") Integer boardId);
    int getRecipeCountByUserId(@Param("userId") Integer userId);
    Optional<Recipe> getRecipeEntityById(@Param("recipeId") Integer recipeId);
    int increaseViewCount(@Param("recipeId") Integer recipeId);
    int modifyRecipe(Recipe recipe);
    int removeRecipe(@Param("recipeId") Integer recipeId,
                     @Param("userId") Integer userId);
}
