package com.nullpoint.fifteenmintable.dto.comment;

import com.nullpoint.fifteenmintable.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddCommentDto {
    private Integer recipeId;
    private Integer userId;
    private String content;

    public Comment toEntity() {
        return Comment.builder()
                .recipeId(recipeId)
                .userId(userId)
                .content(content)
                .build();
    }
}
