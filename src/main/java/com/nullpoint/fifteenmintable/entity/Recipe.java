package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recipe {
    private Integer recipeId;
    private Integer boardId;
    private Integer mainCategoryId;
    private Integer subCategoryId;
    private Integer userId;
    private String title;
    private String intro;
    private String thumbnailImgUrl;
    private String ingredients;
    private String ingredientImgUrl;
    private String steps;
    private Integer viewCount;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    private Board board;
    private MainCategory mainCategory;
    private SubCategory subCategory;
    private List<RecipeRating> recipeRatingList;
    private List<RecipeHashtag> recipeHashtagList;

}
