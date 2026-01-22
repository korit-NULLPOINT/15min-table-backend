package com.nullpoint.fifteenmintable.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRespDto {
    private Integer userId;
    private String username;
    private String profileImgUrl;
    private Integer followersCount;
    private Integer followingsCount;
    private Boolean isFollowing;
}
