package com.nullpoint.fifteenmintable.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostListRespDto {
    private Integer postId;
    private Integer userId;
    private String username;
    private String title;
    private String profileImgUrl;
    private String thumbnailImgUrl;
    private Integer viewCount;
    private Integer commentCount;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
