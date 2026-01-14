package com.nullpoint.fifteenmintable.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FollowRespDto {
    private Integer userId;
    private String username;
    private String profileImgUrl;
    private Integer followId;
    private LocalDateTime createDt;
}
