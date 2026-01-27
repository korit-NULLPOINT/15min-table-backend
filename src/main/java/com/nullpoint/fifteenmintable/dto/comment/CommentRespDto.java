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
    private String targetType;
    private Integer targetId;
    private Integer userId;
    private String username;
    private String title;
    private String content;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
