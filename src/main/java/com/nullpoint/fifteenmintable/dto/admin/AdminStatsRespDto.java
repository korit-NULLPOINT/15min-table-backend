package com.nullpoint.fifteenmintable.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsRespDto {
    private Long totalUsers;
    private Long totalRecipes;
    private Long totalPosts;
}
