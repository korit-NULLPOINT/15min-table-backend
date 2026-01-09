package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRating {
    private Integer recipeRatingId;
    private Integer recipeId;
    private Integer userId;
    private Integer rating;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
