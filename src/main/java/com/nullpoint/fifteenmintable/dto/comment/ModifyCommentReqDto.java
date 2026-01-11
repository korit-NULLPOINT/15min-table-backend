package com.nullpoint.fifteenmintable.dto.comment;

import com.nullpoint.fifteenmintable.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyCommentReqDto {
    private Integer commentId;
    private Integer userId;
    private String content;

    public Comment toEntity() {
        return Comment.builder()
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .build();
    }
}
