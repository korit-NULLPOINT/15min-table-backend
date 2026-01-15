package com.nullpoint.fifteenmintable.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowRespDto {
    private Integer followId;
    private Integer userId;
    private String username;
    private String profileImgUrl;
    private LocalDateTime createDt;
}
