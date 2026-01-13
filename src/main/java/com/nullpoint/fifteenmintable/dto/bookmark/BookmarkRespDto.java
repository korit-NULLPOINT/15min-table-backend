package com.nullpoint.fifteenmintable.dto.bookmark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkRespDto {
    private Integer recipeBookmarkId;
    private Integer userId;
    private Integer recipeId;
    private LocalDateTime createDt;
}
