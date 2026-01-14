package com.nullpoint.fifteenmintable.dto.comment;

import com.nullpoint.fifteenmintable.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyCommentReqDto {
    private Integer commentId;
    private String content;

    public Comment toEntity(Integer userId) {
        return Comment.builder()
                .commentId(commentId)
                .userId(userId)
                .content(content)
                .build();
    }
}
