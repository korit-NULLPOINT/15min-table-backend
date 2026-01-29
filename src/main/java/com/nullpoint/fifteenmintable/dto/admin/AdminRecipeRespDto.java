package com.nullpoint.fifteenmintable.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRecipeRespDto {
    private Integer recipeId;
    private Integer userId;
    private Integer viewCount;
    private Integer commentCount;
    private String title;
    private String username;
    private String profileImgUrl;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
}
