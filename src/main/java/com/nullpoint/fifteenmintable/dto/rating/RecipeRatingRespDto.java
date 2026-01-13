package com.nullpoint.fifteenmintable.dto.rating;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeRatingRespDto {
    private Integer recipeRatingId;
    private Integer recipeId;
    private Integer userId;
    private Integer rating;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
