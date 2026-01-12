package com.nullpoint.fifteenmintable.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRespDto {
    private Integer commentId;
    private Integer recipeId;
    private Integer userId;
    private String content;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
