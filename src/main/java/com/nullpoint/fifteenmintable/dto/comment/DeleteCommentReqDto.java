package com.nullpoint.fifteenmintable.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteCommentReqDto {
    private Integer recipeId;
    private Integer userId;
}
