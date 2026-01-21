package com.nullpoint.fifteenmintable.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRespDto {
    private Integer commentId;
    private Integer recipeId;
    private Integer userId;
    private String username;
    private String profileImgUrl;
    private String content;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
