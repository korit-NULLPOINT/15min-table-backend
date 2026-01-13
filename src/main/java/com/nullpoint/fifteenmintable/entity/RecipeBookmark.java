package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeBookmark {
    private Integer recipeBookmarkId;
    private Integer userId;
    private Integer recipeId;
    private LocalDateTime createDt;
}
