package com.nullpoint.fifteenmintable.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailRespDto {
    private Integer postId;
    private Integer boardId;
    private Integer userId;
    private String username;
    private String profileImgUrl;
    private String title;
    private String content;
    private List<String> imgUrls;
    private Integer viewCount;
    private Integer commentCount;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
