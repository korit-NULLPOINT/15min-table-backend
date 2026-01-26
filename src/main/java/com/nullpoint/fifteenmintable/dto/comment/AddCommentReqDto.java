package com.nullpoint.fifteenmintable.dto.comment;

import com.nullpoint.fifteenmintable.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentReqDto {
    @Schema(example = "RECIPE")
    private String targetType;

    @Schema(example = "1")
    private Integer targetId;

    private String content;

    public Comment toEntity(Integer userId) {
        Comment.CommentBuilder builder = Comment.builder()
                .targetType(targetType)
                .targetId(targetId)
                .userId(userId)
                .content(content);

        // 레시피 댓글이면 기존 호환용 recipe_id도 같이 채움 (듀얼 저장)
        if ("RECIPE".equalsIgnoreCase(targetType)) {
            builder.recipeId(targetId);
        }

        return builder.build();
    }
}
