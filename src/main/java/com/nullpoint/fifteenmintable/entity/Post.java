package com.nullpoint.fifteenmintable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    private Integer postId;
    private Integer boardId;
    private Integer userId;
    private String title;
    private String content;
    private Integer viewCount;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
