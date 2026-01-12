package com.nullpoint.fifteenmintable.dto.comment;

import com.nullpoint.fifteenmintable.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddCommentReqDto {
    private Integer recipeId;
    private String content;
    private Integer userId;

    public Comment toEntity() {
        return Comment.builder()
                .recipeId(recipeId)
                .content(content)
                .userId(userId)
                .build();
    }
}
