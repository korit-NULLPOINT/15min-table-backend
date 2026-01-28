package com.nullpoint.fifteenmintable.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeFilterReqDto {
    private Integer mainCategoryId;
    private Integer subCategoryId;
    private String keyword;
}
