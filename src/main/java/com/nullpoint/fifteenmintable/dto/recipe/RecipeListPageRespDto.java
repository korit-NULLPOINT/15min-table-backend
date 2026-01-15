package com.nullpoint.fifteenmintable.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RecipeListPageRespDto {
    private List<RecipeListRespDto> items;
    private Integer totalCount;
    private Integer page;
    private Integer size;
}
