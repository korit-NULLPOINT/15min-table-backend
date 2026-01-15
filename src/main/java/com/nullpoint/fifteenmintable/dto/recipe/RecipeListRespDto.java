package com.nullpoint.fifteenmintable.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeListRespDto {
    private Integer recipeId;
    private String thumbnailImgUrl;
    private Integer viewCount;
    private Double avgRating;
    private String title;
    private String username;
}
