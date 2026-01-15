package com.nullpoint.fifteenmintable.dto.recipe;

import com.nullpoint.fifteenmintable.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddRecipeReqDto {
    private Integer mainCategoryId;
    private Integer subCategoryId;
    private String title;
    private String intro;
    private String thumbnailImgUrl;
    private String ingredients;
    private String ingredientImgUrl;
    private String steps;

    public Recipe toEntity(Integer boardId) {
        return Recipe.builder()
                .boardId(boardId)
                .mainCategoryId(mainCategoryId)
                .subCategoryId(subCategoryId)
                .title(title)
                .intro(intro)
                .thumbnailImgUrl(thumbnailImgUrl)
                .ingredients(ingredients)
                .ingredientImgUrl(ingredientImgUrl)
                .steps(steps)
                .viewCount(0)
                .build();
    }
}
