package com.nullpoint.fifteenmintable.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowCountRespDto {
    private Integer followerCount;
    private Integer followingCount;
}
