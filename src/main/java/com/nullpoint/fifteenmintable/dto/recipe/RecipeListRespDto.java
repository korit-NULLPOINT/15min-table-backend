package com.nullpoint.fifteenmintable.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeListRespDto {
    private Integer recipeId;
    private Integer userId;
    private String thumbnailImgUrl;
    private Integer viewCount;
    private Double avgRating;
    private String title;
    private String username;
    private String profileImgUrl;
    private Integer mainCategoryId;
    private Integer subCategoryId;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
