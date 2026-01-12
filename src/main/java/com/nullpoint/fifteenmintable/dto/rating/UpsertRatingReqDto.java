package com.nullpoint.fifteenmintable.dto.rating;

import com.nullpoint.fifteenmintable.entity.RecipeRating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertRatingReqDto { // 복합 고유키를 이용해, 값 없으면 insert / 있다면 update
    private Integer recipeId;
    private Integer rating; // 1~5

    public RecipeRating toEntity(Integer userId) {
        return RecipeRating.builder()
                .recipeId(recipeId)
                .userId(userId)
                .rating(rating)
                .build();
    }
}
