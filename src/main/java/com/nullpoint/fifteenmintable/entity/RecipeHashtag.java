package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeHashtag {
    private Integer recipeHashtagId;
    private Integer recipeId;
    private Integer hashtagId;
    private Integer createDt;

    private Hashtag hashtag;
}
