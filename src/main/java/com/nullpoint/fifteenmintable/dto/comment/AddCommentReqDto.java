package com.nullpoint.fifteenmintable.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nullpoint.fifteenmintable.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentReqDto {

    @JsonIgnore
    @Schema(hidden = true)
    private String targetType;

    @JsonIgnore
    @Schema(hidden = true)
    private Integer targetId;

    private String content;

    public Comment toEntity(Integer userId) {
        return Comment.builder()
                .targetType(targetType)
                .targetId(targetId)
                .userId(userId)
                .content(content)
                .build();
    }
}
