package com.nullpoint.fifteenmintable.dto.hashtag;

import lombok.Data;

import java.util.List;

@Data
public class AddRecipeHashtagsReqDto {
    private Integer recipeId;
    private List<String> hashtagNames;
}
