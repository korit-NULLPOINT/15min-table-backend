package com.nullpoint.fifteenmintable.dto.recipe;

import com.nullpoint.fifteenmintable.dto.hashtag.HashtagRespDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailRespDto {
    private Integer recipeId;
    private Integer boardId;
    private Integer mainCategoryId;
    private Integer subCategoryId;
    private Integer userId;
    private String username;
    private String profileImgUrl;

    private String title;
    private String intro;
    private String thumbnailImgUrl;
    private String ingredients;
    private String ingredientImgUrl;
    private String steps;

    private Integer viewCount;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;

    private Double avgRating;
    private Integer ratingCount;
    private Integer commentCount;

    private List<HashtagRespDto> hashtags;
}
