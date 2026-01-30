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

public class PostImg {
    private Integer postImgId;
    private Integer postId;
    private String imgUrl;
    private Integer sortOrder;
    private LocalDateTime createDt;
}
