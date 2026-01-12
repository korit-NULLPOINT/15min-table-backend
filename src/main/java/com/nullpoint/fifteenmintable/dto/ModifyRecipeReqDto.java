package com.nullpoint.fifteenmintable.dto;

import com.nullpoint.fifteenmintable.entity.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyRecipeReqDto {
    private Integer recipeId;
    private Integer mainCategoryId;
    private Integer subCategoryId;
    private String title;
    private String intro;
    private String thumbnailImgUrl;
    private String ingredients;
    private String ingredientImgUrl;
    private String steps;

    public Recipe toEntity() {
        return Recipe.builder().build();
    }
}
